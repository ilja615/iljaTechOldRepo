package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextRenderingUtils {
   @VisibleForTesting
   protected static List<String> lineBreak(String p_225223_0_) {
      return Arrays.asList(p_225223_0_.split("\\n"));
   }

   public static List<TextRenderingUtils.Line> decompose(String p_225224_0_, TextRenderingUtils.LineSegment... p_225224_1_) {
      return decompose(p_225224_0_, Arrays.asList(p_225224_1_));
   }

   private static List<TextRenderingUtils.Line> decompose(String p_225225_0_, List<TextRenderingUtils.LineSegment> p_225225_1_) {
      List<String> list = lineBreak(p_225225_0_);
      return insertLinks(list, p_225225_1_);
   }

   private static List<TextRenderingUtils.Line> insertLinks(List<String> p_225222_0_, List<TextRenderingUtils.LineSegment> p_225222_1_) {
      int i = 0;
      List<TextRenderingUtils.Line> list = Lists.newArrayList();

      for(String s : p_225222_0_) {
         List<TextRenderingUtils.LineSegment> list1 = Lists.newArrayList();

         for(String s1 : split(s, "%link")) {
            if ("%link".equals(s1)) {
               list1.add(p_225222_1_.get(i++));
            } else {
               list1.add(TextRenderingUtils.LineSegment.text(s1));
            }
         }

         list.add(new TextRenderingUtils.Line(list1));
      }

      return list;
   }

   public static List<String> split(String p_225226_0_, String p_225226_1_) {
      if (p_225226_1_.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List<String> list = Lists.newArrayList();

         int i;
         int j;
         for(i = 0; (j = p_225226_0_.indexOf(p_225226_1_, i)) != -1; i = j + p_225226_1_.length()) {
            if (j > i) {
               list.add(p_225226_0_.substring(i, j));
            }

            list.add(p_225226_1_);
         }

         if (i < p_225226_0_.length()) {
            list.add(p_225226_0_.substring(i));
         }

         return list;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Line {
      public final List<TextRenderingUtils.LineSegment> segments;

      Line(List<TextRenderingUtils.LineSegment> p_i51644_1_) {
         this.segments = p_i51644_1_;
      }

      public String toString() {
         return "Line{segments=" + this.segments + '}';
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            TextRenderingUtils.Line textrenderingutils$line = (TextRenderingUtils.Line)p_equals_1_;
            return Objects.equals(this.segments, textrenderingutils$line.segments);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.segments);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LineSegment {
      private final String fullText;
      private final String linkTitle;
      private final String linkUrl;

      private LineSegment(String p_i51642_1_) {
         this.fullText = p_i51642_1_;
         this.linkTitle = null;
         this.linkUrl = null;
      }

      private LineSegment(String p_i51643_1_, String p_i51643_2_, String p_i51643_3_) {
         this.fullText = p_i51643_1_;
         this.linkTitle = p_i51643_2_;
         this.linkUrl = p_i51643_3_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            TextRenderingUtils.LineSegment textrenderingutils$linesegment = (TextRenderingUtils.LineSegment)p_equals_1_;
            return Objects.equals(this.fullText, textrenderingutils$linesegment.fullText) && Objects.equals(this.linkTitle, textrenderingutils$linesegment.linkTitle) && Objects.equals(this.linkUrl, textrenderingutils$linesegment.linkUrl);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.fullText, this.linkTitle, this.linkUrl);
      }

      public String toString() {
         return "Segment{fullText='" + this.fullText + '\'' + ", linkTitle='" + this.linkTitle + '\'' + ", linkUrl='" + this.linkUrl + '\'' + '}';
      }

      public String renderedText() {
         return this.isLink() ? this.linkTitle : this.fullText;
      }

      public boolean isLink() {
         return this.linkTitle != null;
      }

      public String getLinkUrl() {
         if (!this.isLink()) {
            throw new IllegalStateException("Not a link: " + this);
         } else {
            return this.linkUrl;
         }
      }

      public static TextRenderingUtils.LineSegment link(String p_225214_0_, String p_225214_1_) {
         return new TextRenderingUtils.LineSegment((String)null, p_225214_0_, p_225214_1_);
      }

      @VisibleForTesting
      protected static TextRenderingUtils.LineSegment text(String p_225218_0_) {
         return new TextRenderingUtils.LineSegment(p_225218_0_);
      }
   }
}
