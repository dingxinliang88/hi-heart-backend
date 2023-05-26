package com.juzi.heart.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.juzi.heart.common.PageRequest;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.manager.AuthManager;
import com.juzi.heart.manager.UserManager;
import com.juzi.heart.mapper.TagMapper;
import com.juzi.heart.mapper.UserMapper;
import com.juzi.heart.model.dto.user.UserLoginRequest;
import com.juzi.heart.model.dto.user.UserRegisterRequest;
import com.juzi.heart.model.dto.user.UserUpdateRequest;
import com.juzi.heart.model.entity.Tag;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.UserService;
import com.juzi.heart.utils.TagSimilarityCalculatorUtils;
import com.juzi.heart.utils.ThrowUtils;
import com.juzi.heart.utils.ValidCheckUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.juzi.heart.constant.BusinessConstants.DEFAULT_PAGE_NUM;
import static com.juzi.heart.constant.BusinessConstants.DEFAULT_PAGE_SIZE;
import static com.juzi.heart.constant.UserConstants.*;
import static com.juzi.heart.constant.UserRedisConstants.CACHE_INDEX_PAGE_USER_KEY_PREFIX;
import static com.juzi.heart.constant.UserRedisConstants.INDEX_CACHE_TTL;

/**
 * @author codejuzi
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-05-15 20:18:05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private AuthManager authManager;

    @Resource
    private UserManager userManager;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final Gson GSON = new Gson();

    /**
     * 父标签权重
     */
    private static final Map<Long, Double> PARENT_TAG_WEIGHTS = new HashMap<>() {{
        // 学习方向
        put(1L, 5.0);
        // 目标
        put(7L, 3.0);
        // 状态
        put(12L, 1.0);
        // 阶段
        put(17L, 1.0);
    }};

    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(userRegisterRequest), StatusCode.PARAMS_ERROR, "注册参数不能为空！");
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkedPassword = userRegisterRequest.getCheckedPassword();
        ValidCheckUtils.checkRegisterParams(userAccount, userPassword, checkedPassword);
        synchronized (userAccount.intern()) {
            // 6. 账号不能重复
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserAccount, userAccount);
            User user = this.getOne(userLambdaQueryWrapper);
            ThrowUtils.throwIf(!Objects.isNull(user), StatusCode.PARAMS_ERROR, "该账号已经存在！");

            // 加密密码
            String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

            // 封装数据
            User newUser = new User();
            newUser.setUserName(DEFAULT_UNAME_PREFIX + RandomStringUtils.randomAlphabetic(DEFAULT_UNAME_SUFFIX_LEN));
            newUser.setUserAccount(userAccount);
            newUser.setUserPassword(encryptedPassword);
            newUser.setUserAvatar(DEFAULT_AVATAR_URL);
            newUser.setGender(MAN);
            // 插入数据
            boolean result = this.save(newUser);
            ThrowUtils.throwIf(!result, StatusCode.SYSTEM_ERROR, "插入数据失败");
            // 返回新用户id
            return newUser.getId();
        }
    }

    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(userLoginRequest), StatusCode.PARAMS_ERROR, "登录信息不能为空");
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        ValidCheckUtils.checkLoginParams(userAccount, userPassword);

        synchronized (userAccount.intern()) {
            // 校验密码是否正确
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            User userFromDb = this.getOne(queryWrapper);
            ThrowUtils.throwIf(Objects.isNull(userFromDb), StatusCode.NOT_FOUND_ERROR, "账号尚未注册！");
            String pwdFromDb = userFromDb.getUserPassword();
            String encryptedPwd = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
            ThrowUtils.throwIf(!pwdFromDb.equals(encryptedPwd), StatusCode.PARAMS_ERROR, "密码不正确！");

            // 用户信息脱敏
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userFromDb, userVO);

            // 保存用户登录态
            HttpSession session = request.getSession();
            session.setAttribute(USER_LOGIN_STATUS_KEY, userVO);

            // 返回脱敏后的用户信息
            return userVO;
        }
    }

    @Override
    public List<User> queryUser(String searchText) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like(User::getUserName, searchText)
                    .or()
                    .like(User::getUserAccount, searchText);
        }
        return this.list(queryWrapper);
    }

    @Override
    public Boolean deleteUserById(Long userId) {
        ThrowUtils.throwIf(userId <= 0, StatusCode.PARAMS_ERROR, "用户id不合法");
        return this.removeById(userId);
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        UserVO loginUser = userManager.getLoginUser(request);
        ThrowUtils.throwIf(Objects.isNull(loginUser), StatusCode.NOT_LOGIN_ERROR, "当前尚未登录");
        HttpSession session = request.getSession();
        session.removeAttribute(USER_LOGIN_STATUS_KEY);
        return true;
    }

    @Override
    public List<UserVO> listUserVO() {
        List<User> userList = this.list();
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public List<UserVO> queryUserByTagList(List<String> tagList) {
        return queryUserByTagListUseSql(tagList);
    }

    @Override
    public List<UserVO> queryUserByTagListUseSql(List<String> tagList) {
        ThrowUtils.throwIf(CollectionUtils.isEmpty(tagList), StatusCode.PARAMS_ERROR, "查询标签为空");
        // like 连接
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        for (String tag : tagList) {
            queryWrapper.like(User::getTags, tag);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public List<UserVO> queryUserByTagListUseMemory(List<String> tagList) {
        ThrowUtils.throwIf(CollectionUtils.isEmpty(tagList), StatusCode.PARAMS_ERROR, "查询标签为空");
        // 先查出所有的用户
        List<User> userList = this.list();
        // 在内存中过滤出匹配的用户
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            @SuppressWarnings("UnstableApiUsage")
            Type setType = new TypeToken<Set<String>>() {
            }.getType();
            Set<String> tmpTagNameSet = GSON.fromJson(tagStr, setType);
            // 判空
            tmpTagNameSet = Optional.ofNullable(tmpTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagList) {
                if (!tmpTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(userUpdateRequest), StatusCode.PARAMS_ERROR, "用户修改信息为空！");
        Long id = userUpdateRequest.getId();
        authManager.adminOrMe(id, request);

        User userFromDb = this.getById(id);

        if (!needUpdate(userFromDb, userUpdateRequest)) {
            return Boolean.TRUE;
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id);

        String userName = userUpdateRequest.getUserName();
        updateWrapper.set(StringUtils.isNotBlank(userName), User::getUserName, userName);

        String userAvatar = userUpdateRequest.getUserAvatar();
        updateWrapper.set(StringUtils.isNotBlank(userAvatar), User::getUserAvatar, userAvatar);

        String userProfile = userUpdateRequest.getUserProfile();
        updateWrapper.set(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile);

        Integer gender = userUpdateRequest.getGender();
        updateWrapper.set(MAN.equals(gender) || WOMAN.equals(gender), User::getGender, gender);

        String phone = userUpdateRequest.getPhone();
        updateWrapper.set(StringUtils.isNotBlank(phone), User::getPhone, phone);

        String email = userUpdateRequest.getEmail();
        updateWrapper.set(StringUtils.isNotBlank(email), User::getEmail, email);

        List<String> tagList = userUpdateRequest.getTagList();
        if (!CollectionUtils.isEmpty(tagList)) {
            String tags = GSON.toJson(tagList);
            updateWrapper.set(StringUtils.isNotBlank(tags), User::getTags, tags);
        }

        boolean updateRes = this.update(updateWrapper);

        // 更新用户的登录态
        User user = this.getById(id);
        UserVO userVO = this.getUserVO(user);
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATUS_KEY, userVO);

        return updateRes;
    }

    @Override
    public UserVO getUserVO(User originUser) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(originUser, userVO);
        return userVO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<UserVO> listUserVOByPage(PageRequest pageRequest, HttpServletRequest request) {
        UserVO loginUser = userManager.getLoginUserPermitNull(request);
        // 未登录，则默认展示1L号用户的推荐名单
        Long loginUserId = 2L;
        if (!Objects.isNull(loginUser)) {
            // 已登录
            loginUserId = loginUser.getId();
        }
        // 读缓存
        String recommendUserKey = String.format("%s:%s", CACHE_INDEX_PAGE_USER_KEY_PREFIX, loginUserId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object cachePage = valueOperations.get(recommendUserKey);
        ThrowUtils.throwIf(!Objects.isNull(cachePage) && !(cachePage instanceof Page),
                StatusCode.SYSTEM_ERROR, "缓存出错！");
        // 缓存不空，直接返回
        if (!Objects.isNull(cachePage)) {
            return (Page<UserVO>) cachePage;
        }
        Page<UserVO> userVOPage = this.doGetUserVOPage(pageRequest);
        // 写缓存
        try {
            valueOperations.set(recommendUserKey, userVOPage, INDEX_CACHE_TTL, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("recommend users, write cache error,", e);
        }
        return userVOPage;
    }

    @Override
    public Page<UserVO> recommendMatchUsers(HttpServletRequest request) {
        UserVO loginUser = userManager.getLoginUser(request);
        String tags = loginUser.getTags();
        ThrowUtils.throwIf(StringUtils.isBlank(tags), StatusCode.OPERATION_ERROR, "您还没有选择您的标签！");

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(User::getId, loginUser.getId())
                .isNotNull(User::getTags);
        List<User> userList = this.list(queryWrapper);

        @SuppressWarnings("UnstableApiUsage")
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> loginUserTagList = GSON.fromJson(tags, type);
        Map<Long, List<Long>> loginUserPIdChildTagIdListMap = this.getPIdChildTagIdListMap(loginUserTagList);
        List<Pair<User, Double>> similarityList = new ArrayList<>();
        for (User user : userList) {
            List<String> userTagList = GSON.fromJson(user.getTags(), type);
            Map<Long, List<Long>> userPIdChildTagIdListMap = this.getPIdChildTagIdListMap(userTagList);
            // 计算相似度
            double similarity = TagSimilarityCalculatorUtils.calculateSimilarity(loginUserPIdChildTagIdListMap,
                    userPIdChildTagIdListMap, PARENT_TAG_WEIGHTS);
            similarityList.add(new Pair<>(user, similarity));
        }
        // 按照相似度，从大到小排序，取前10个
        List<Pair<User, Double>> topUserPairList = similarityList.stream()
                .sorted(Comparator.comparing(Pair::getValue, Comparator.reverseOrder()))
                .limit(DEFAULT_PAGE_SIZE)
                .collect(Collectors.toList());

        List<UserVO> userVOList = topUserPairList.stream()
                .map(pair -> getUserVO(pair.getKey()))
                .collect(Collectors.toList());

        Page<UserVO> userVOPage = new Page<>(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, userVOList.size());
        userVOPage.setRecords(userVOList);

        return userVOPage;
    }

    @Override
    public Page<UserVO> doGetUserVOPage(PageRequest pageRequest) {
        Integer pageNum = pageRequest.getPageNum();
        Integer pageSize = pageRequest.getPageSize();
        pageSize = Objects.isNull(pageSize) || pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
        pageNum = Objects.isNull(pageNum) || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;
        Page<User> userPage = this.page(new Page<>(pageNum, pageSize));
        List<UserVO> userVOList = userPage.getRecords().stream().map(this::getUserVO).collect(Collectors.toList());
        Page<UserVO> userVOPage = new Page<>(
                userPage.getCurrent(), userPage.getSize(), userPage.getTotal()
        );
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    @Override
    public Map<Long, List<Long>> getPIdChildTagIdListMap(List<String> childTagNameList) {
        List<Tag> childTagList = tagMapper.getChildTagByTagName(childTagNameList);
        Map<Long, List<Long>> parentIdToChildIdsMap = new HashMap<>();
        for (Tag childTag : childTagList) {
            Long parentId = childTag.getParentId();
            List<Long> childIds = parentIdToChildIdsMap.getOrDefault(parentId, new ArrayList<>());
            childIds.add(childTag.getId());
            parentIdToChildIdsMap.put(parentId, childIds);
        }
        return parentIdToChildIdsMap;
    }

    /**
     * 检测是否需要更新用户信息
     *
     * @param userFromDb        数据库中的用户信息
     * @param userUpdateRequest 用户更新请求封装信息
     * @return true - 需要更新
     */
    private boolean needUpdate(User userFromDb, UserUpdateRequest userUpdateRequest) {
        String userName = userUpdateRequest.getUserName();
        String userAvatar = userUpdateRequest.getUserAvatar();
        String userProfile = userUpdateRequest.getUserProfile();
        Integer gender = userUpdateRequest.getGender();
        String phone = userUpdateRequest.getPhone();
        String email = userUpdateRequest.getEmail();
        List<String> tagList = userUpdateRequest.getTagList();

        boolean eUserName = false;
        if (StringUtils.isNotBlank(userName)) {
            eUserName = userFromDb.getUserName().equals(userName);
        }

        boolean eUserAvatar = false;
        if (StringUtils.isNotBlank(userAvatar)) {
            eUserAvatar = userFromDb.getUserAvatar().equals(userAvatar);
        }

        boolean eUserProfile = false;
        if (StringUtils.isNotBlank(userProfile)) {
            eUserProfile = userProfile.equals(userFromDb.getUserProfile());
        }

        boolean ePhone = false;
        if (StringUtils.isNotBlank(phone)) {
            ePhone = phone.equals(userFromDb.getPhone());
        }

        boolean eEmail = false;
        if (StringUtils.isNotBlank(email)) {
            eEmail = email.equals(userFromDb.getEmail());
        }

        boolean eGender = false;
        if (MAN.equals(gender) || WOMAN.equals(gender)) {
            eGender = userFromDb.getGender().equals(gender);
        }

        boolean eTag = false;
        if (!CollectionUtils.isEmpty(tagList)) {
            String tagJson = GSON.toJson(tagList);
            eTag = tagJson.equals(userFromDb.getTags());
        }

        return !(eUserName && eUserAvatar && eUserProfile && eEmail && ePhone && eGender && eTag);
    }
}




