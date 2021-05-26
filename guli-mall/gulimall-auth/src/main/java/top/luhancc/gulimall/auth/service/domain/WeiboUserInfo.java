package top.luhancc.gulimall.auth.service.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * 微博用户的基本信息
 *
 * @author luHan
 * @create 2021/1/12 10:51
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "screen_name",
        "name",
        "province",
        "city",
        "location",
        "description",
        "url",
        "profile_image_url",
        "domain",
        "gender",
        "followers_count",
        "friends_count",
        "statuses_count",
        "favourites_count",
        "created_at",
        "following",
        "allow_all_act_msg",
        "geo_enabled",
        "verified",
        "status",
        "allow_all_comment",
        "avatar_large",
        "verified_reason",
        "follow_me",
        "online_status",
        "bi_followers_count"
})
@Data
public class WeiboUserInfo {
    /**
     * 用户UID
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 用户昵称
     */
    @JsonProperty("screen_name")
    private String screenName;

    /**
     * 友好显示名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 用户所在省级ID
     */
    @JsonProperty("province")
    private String province;

    /**
     * 用户所在城市ID
     */
    @JsonProperty("city")
    private String city;

    /**
     * 用户所在地
     */
    @JsonProperty("location")
    private String location;

    /**
     * 用户个人描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 用户博客地址
     */
    @JsonProperty("url")
    private String url;

    /**
     * 用户头像地址（中图），50×50像素
     */
    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    /**
     * 用户的个性化域名
     */
    @JsonProperty("domain")
    private String domain;

    /**
     * 性别，m：男、f：女、n：未知
     */
    @JsonProperty("gender")
    private String gender;

    /**
     * 粉丝数
     */
    @JsonProperty("followers_count")
    private Integer followersCount;

    /**
     * 关注数
     */
    @JsonProperty("friends_count")
    private Integer friendsCount;

    /**
     * 微博数
     */
    @JsonProperty("statuses_count")
    private Integer statusesCount;

    /**
     * 收藏数
     */
    @JsonProperty("favourites_count")
    private Integer favouritesCount;

    /**
     * 用户创建（注册）时间
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * 是否允许所有人给我发私信，true：是，false：否
     */
    @JsonProperty("allow_all_act_msg")
    private Boolean allowAllActMsg;

    /**
     * 是否允许标识用户的地理位置，true：是，false：否
     */
    @JsonProperty("geo_enabled")
    private Boolean geoEnabled;

    /**
     * 是否是微博认证用户，即加V用户，true：是，false：否
     */
    @JsonProperty("verified")
    private Boolean verified;

    /**
     * 是否允许所有人对我的微博进行评论，true：是，false：否
     */
    @JsonProperty("allow_all_comment")
    private Boolean allowAllComment;

    /**
     * 用户头像地址（大图），180×180像素
     */
    @JsonProperty("avatar_large")
    private String avatarLarge;

    /**
     * 认证原因
     */
    @JsonProperty("verified_reason")
    private String verifiedReason;

    /**
     * 该用户是否关注当前登录用户，true：是，false：否
     */
    @JsonProperty("follow_me")
    private Boolean followMe;

    /**
     * 用户的在线状态，0：不在线、1：在线
     */
    @JsonProperty("online_status")
    private Integer onlineStatus;

    /**
     * 用户的互粉数
     */
    @JsonProperty("bi_followers_count")
    private Integer biFollowersCount;
}
