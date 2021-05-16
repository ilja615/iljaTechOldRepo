package net.minecraft.world.server;

import java.util.Objects;

public final class Ticket<T> implements Comparable<Ticket<?>> {
   private final TicketType<T> type;
   private final int ticketLevel;
   private final T key;
   private long createdTick;

   protected Ticket(TicketType<T> p_i226095_1_, int p_i226095_2_, T p_i226095_3_) {
      this.type = p_i226095_1_;
      this.ticketLevel = p_i226095_2_;
      this.key = p_i226095_3_;
   }

   public int compareTo(Ticket<?> p_compareTo_1_) {
      int i = Integer.compare(this.ticketLevel, p_compareTo_1_.ticketLevel);
      if (i != 0) {
         return i;
      } else {
         int j = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(p_compareTo_1_.type));
         return j != 0 ? j : this.type.getComparator().compare(this.key, (T)p_compareTo_1_.key);
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Ticket)) {
         return false;
      } else {
         Ticket<?> ticket = (Ticket)p_equals_1_;
         return this.ticketLevel == ticket.ticketLevel && Objects.equals(this.type, ticket.type) && Objects.equals(this.key, ticket.key);
      }
   }

   public int hashCode() {
      return Objects.hash(this.type, this.ticketLevel, this.key);
   }

   public String toString() {
      return "Ticket[" + this.type + " " + this.ticketLevel + " (" + this.key + ")] at " + this.createdTick;
   }

   public TicketType<T> getType() {
      return this.type;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   protected void setCreatedTick(long p_229861_1_) {
      this.createdTick = p_229861_1_;
   }

   protected boolean timedOut(long p_223182_1_) {
      long i = this.type.timeout();
      return i != 0L && p_223182_1_ - this.createdTick > i;
   }
}
