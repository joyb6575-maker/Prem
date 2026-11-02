package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun premStrings_bilingualSupport_returnsCorrectTranslations() {
    val bnTagline = com.example.data.PremStrings.get("tagline", com.example.model.AppLanguage.BN)
    val enTagline = com.example.data.PremStrings.get("tagline", com.example.model.AppLanguage.EN)
    assertTrue(bnTagline.contains("ভালোবাসার গল্প"))
    assertTrue(enTagline.contains("love story"))
  }

  @Test
  fun bangladeshiData_containsAllDivisions() {
    assertEquals(8, com.example.data.BangladeshiData.divisions.size)
    assertTrue(com.example.data.BangladeshiData.divisions.contains("Dhaka"))
    assertTrue(com.example.data.BangladeshiData.divisions.contains("Chattogram"))
    assertTrue(com.example.data.BangladeshiData.divisions.contains("Sylhet"))
  }

  @Test
  fun seedProfiles_areAllAdults() {
    com.example.data.BangladeshiData.seedProfiles.forEach { profile ->
      assertTrue("Profile ${profile.fullName} must be 18+", profile.age >= 18)
    }
  }
}
