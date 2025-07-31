package com.hearthappy.vma.model.testmodel;


import java.io.Serializable;
import java.util.List;

public class DynamicDTO implements Serializable {
    public String c_time;
    public Integer comment_num;
    public String content;
    public String cover_img;
    public Integer id;
    public List<String> imgs;
    public Integer is_fork;
    public Integer is_give_like;
    public Integer is_show_location;
    public Integer popularity;
    public String proportion;
    public Integer support;
    public String type_name;
    public UserDTO user;
    public String topic_id = "";
    public String topic_title = "";
    public UserDynamicLocationDTO userDynamicLocation;
    public Integer user_id;
    public String video;
    public Integer is_attention;
    public Integer in_live;
    public String in_live_room_id;
    public int top_sort;
    public String head_portrait_box;
    public String year;
    public ResHotCommentList hot_comment;


    /**
     * //只在局部刷新的时候使用  0 不处理, 1 点赞刷新  2 评论数刷新
     */
    public Integer type = 0;

    public static class ResHotComment implements Serializable {
        public String c_time;
        public String content;
        public Integer id;
        public Integer is_author;
        public int is_give_like;
        public int support_num;
        public UserDTO user;
        public int user_dynamic_id;
        public int user_id;
    }

    public static class UserDTO implements Serializable {
        public String avatar;
        public Integer id;
        public Integer is_recommend;
        public Integer level;
        public String nickname;
        public String icon;
        public Integer sex;
        public int age;
        public Integer status;
        public String level_icon;
        public String charm_level_icon;
        public Integer charm_level;
        public Integer ifblack;//0:拉黑 1:未拉黑
        public NobilityUserBean nobility_user;

        public UserVipLevelDTO userVipLevel;


        public static class UserVipLevelDTO implements Serializable {
            public Integer experience;
            public String icon;
            public Integer id;
            public Integer level;
        }


        public static class NobilityUserBean implements Serializable {
            public String nobility_id;
            public String nobility_name;
            public String nobility_level;
            public String nobility_img;
            public Long end_time;
            public String end_time_format;
            public int nobility_img_switch;
        }

    }

    public static class UserDynamicLocationDTO implements Serializable {
        public Integer id;
        public String lat;
        public String lng;
        public String name;
        public Integer user_dynamic_id;
    }


    public void setIs_attention(Integer is_attention) {
        this.is_attention = is_attention;
    }

    public void setIs_give_like(Integer is_give_like) {
        this.is_give_like = is_give_like;
    }

    public void setSupport(Integer support) {
        this.support = support;
    }

    public void setComment_num(Integer comment_num) {
        this.comment_num = comment_num;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}