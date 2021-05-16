package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CSVWriter {
   private final Writer output;
   private final int columnCount;

   private CSVWriter(Writer p_i51695_1_, List<String> p_i51695_2_) throws IOException {
      this.output = p_i51695_1_;
      this.columnCount = p_i51695_2_.size();
      this.writeLine(p_i51695_2_.stream());
   }

   public static CSVWriter.Builder builder() {
      return new CSVWriter.Builder();
   }

   public void writeRow(Object... p_225426_1_) throws IOException {
      if (p_225426_1_.length != this.columnCount) {
         throw new IllegalArgumentException("Invalid number of columns, expected " + this.columnCount + ", but got " + p_225426_1_.length);
      } else {
         this.writeLine(Stream.of(p_225426_1_));
      }
   }

   private void writeLine(Stream<?> p_225427_1_) throws IOException {
      this.output.write((String)p_225427_1_.map(CSVWriter::getStringValue).collect(Collectors.joining(",")) + "\r\n");
   }

   private static String getStringValue(@Nullable Object p_225425_0_) {
      return StringEscapeUtils.escapeCsv(p_225425_0_ != null ? p_225425_0_.toString() : "[null]");
   }

   public static class Builder {
      private final List<String> headers = Lists.newArrayList();

      public CSVWriter.Builder addColumn(String p_225423_1_) {
         this.headers.add(p_225423_1_);
         return this;
      }

      public CSVWriter build(Writer p_225422_1_) throws IOException {
         return new CSVWriter(p_225422_1_, this.headers);
      }
   }
}
