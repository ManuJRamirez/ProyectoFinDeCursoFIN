package learning.tokioschool.proyectoJuegoCombateEsdla.personajes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Faccion;
import learning.tokioschool.proyectoJuegoCombateEsdla.modelado.personajes.Personaje;

// TODO: Auto-generated Javadoc
/**
 * The Class PersonajeCombateTest.
 */
public class PersonajeCombateTest {
	
	/**
	 * Crear atacante.
	 *
	 * @param danioFijo the danio fijo
	 * @param armadura the armadura
	 * @return the personaje
	 */
	private Personaje crearAtacante(int danioFijo, int armadura) {
		return new Personaje("Atacante", 100, armadura , 1 , 3, Faccion.HEROES) {
			@Override
			public int calcularAtaque(Personaje enemigo) {
				return danioFijo;
			}
		};
	}
	
	/**
	 * Crear defensor.
	 *
	 * @param vida the vida
	 * @param armadura the armadura
	 * @return the personaje
	 */
	private Personaje crearDefensor(int vida, int armadura) {
		return new Personaje("Defensor", vida, armadura, 1, 3, Faccion.BESTIAS) {
			@Override
			public int calcularAtaque(Personaje enemigo) {
				return 0;
			}
		};
	}
	
	/**
	 * El danio final resta la armadura del enemigo.
	 */
	@Test
	void elDanioFinalRestaLaArmaduraDelEnemigo() {
		Personaje atacante = crearAtacante(50 , 0);
		Personaje defensor = crearDefensor(100, 20);
		int danio = atacante.calcularDanioFinal(defensor, null);
		
		assertEquals(30,danio);
	}
	
	/**
	 * El danio minimo es cero nunca negativo.
	 */
	@Test
	void elDanioMinimoEsCeroNuncaNegativo() {
		Personaje atacante = crearAtacante(10, 0);
		Personaje defensor = crearDefensor(100, 1000);
		int danio = atacante.calcularDanioFinal(defensor, null);
		
		assertEquals(0, danio);
	}

	/**
	 * Si la armadura es igual aldanio el resultado es cero.
	 */
	@Test
	void siLaArmaduraEsIgualAldanioElResultadoEsCero() {
		Personaje atacante = crearAtacante(40, 0);
		Personaje defensor = crearDefensor(100, 40);
		int danio = atacante.calcularDanioFinal(defensor, null);
		
		assertEquals(0, danio);
	}
	
	/**
	 * Recibir danio nunca deja la vida por debajo de cero.
	 */
	@Test
	void recibirDanioNuncaDejaLaVidaPorDebajoDeCero() {
		Personaje defensor = crearDefensor(10, 0);
		
		defensor.recibirDanio(999);
		
		assertEquals(0, defensor.getVida());
		assertTrue(!defensor.estaVivo());
	}
	
	/**
	 * Defensor ha sido atacado esta ronda.
	 */
	@Test
	void defensorHaSidoAtacadoEstaRonda() {
		Personaje defensor = crearDefensor(50, 0);
		
		assertTrue(!defensor.isHasidoAtacadoEstaRonda());
		
		defensor.recibirDanio(5);
		
		assertTrue(defensor.isHasidoAtacadoEstaRonda());
	}
	
	/**
	 * Un personaje con vida mayor que cero esta vido.
	 */
	@Test
	void unPersonajeConVidaMayorQueCeroEstaVido() {
		Personaje defensor = crearDefensor(1, 0);
		
		assertTrue(defensor.estaVivo());
		
		defensor.recibirDanio(1);
		
		assertTrue(!defensor.estaVivo());
	}
}
