package net.minecraft.crash;

public class ReportedException extends RuntimeException {
   private final CrashReport report;

   public ReportedException(CrashReport p_i1356_1_) {
      this.report = p_i1356_1_;
   }

   public CrashReport getReport() {
      return this.report;
   }

   public Throwable getCause() {
      return this.report.getException();
   }

   public String getMessage() {
      return this.report.getTitle();
   }
}
