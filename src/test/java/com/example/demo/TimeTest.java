package com.example.demo;


import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class TimeTest {

  private List<Integer> accountList = List.of(666, 777, 888, 999, 1999);

  @SneakyThrows
  @Test
  public void buildEventDay() {

    for (int i = 1; i <= 100; i++) {
      LocalDateTime ldt = LocalDateTime.of(2020, Month.JULY, 20, 0, 0, 0);

      LocalDateTime ldtTransaction = LocalDateTime.of(2020, Month.JULY, 20, 0, 0, 0);

      ldt = ldt.plusMinutes(i * 5);
      ldt = ldt.plusSeconds(i * 20);
      ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/Mexico_City"));


      ldtTransaction = ldtTransaction.plusMinutes(i * 2);
      ldtTransaction = ldtTransaction.plusSeconds(i * 10);
      ZonedDateTime zdtTransaction = ldtTransaction.atZone(ZoneId.of("America/Mexico_City"));

      System.out.println("transactio time = "+zdtTransaction + " balance time "+ zdt);


    }
  }


  @Test
  public void testAccounts() {

    int number = getRandomNumberUsingNextInt(0, accountList.size() - 1);

    System.out.println(accountList.get(number));

  }

  public int getRandomNumberUsingNextInt(int min, int max) {
    Random random = new Random();
    return random.nextInt(max - min) + min;
  }
}
