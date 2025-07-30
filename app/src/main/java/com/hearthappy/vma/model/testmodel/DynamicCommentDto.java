package com.hearthappy.vma.model.testmodel;

import java.util.List;

public class DynamicCommentDto {

   public Integer count;
   public Integer next;
   public List<ComnentDto> record;

   public static class ComnentDto {
      public String c_time;
      public String content;
      public Integer id;
      public Integer is_author;//1就是作者
      public Integer is_give_like;
      public Integer support_num;
      public UserDTO user;
      public Integer user_id;

      public static class UserDTO {
         public String avatar;
         public String level_icon;
         public String charm_level_icon;
         public String c_time;
         public Integer id;
         public String nickname;
         public Integer sex;
      }

      public void setIs_give_like(Integer is_give_like) {
         this.is_give_like = is_give_like;
      }

      public void setSupport_num(Integer support_num) {
         this.support_num = support_num;
      }
   }
}
