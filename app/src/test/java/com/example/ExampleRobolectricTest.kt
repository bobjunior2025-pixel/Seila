package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.DefaultDataGenerator
import com.example.engine.SimulationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ReinoFoot", appName)
  }

  @Test
  fun `test medieval generator creates 16 kingdoms and fixtures`() {
    val kingdoms = DefaultDataGenerator.generateInitialKingdoms()
    assertEquals(16, kingdoms.size)

    val div1 = kingdoms.filter { it.division == 1 }
    val div2 = kingdoms.filter { it.division == 2 }
    assertEquals(8, div1.size)
    assertEquals(8, div2.size)

    val matches = DefaultDataGenerator.generateLeagueFixtures(kingdoms, 1)
    // 8 times em turno e returno = 14 rodadas x 4 jogos por rodada = 56 jogos
    assertEquals(56, matches.size)
  }

  @Test
  fun `test team combat power calculation`() {
    val kingdoms = DefaultDataGenerator.generateInitialKingdoms()
    val paladinos = kingdoms.first { it.id == "paladinos" }
    val power = SimulationEngine.calculateTeamPower(paladinos, isHome = true)

    assertTrue(power.totalPower > 50f)
    assertNotNull(power.attackPower)
    assertNotNull(power.defensePower)
  }
}
