import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del ciclo 2 de SlotMachine (siempre en modo
 * invisible): intercambiar, fijar y soltar, girar pasos y dejar una
 * configuración. La máquina de prueba tiene dos ruedas: la 1 con red y
 * blue (muestra red) y la 2 con green, yellow y pink (muestra green).
 */
public class SlotMachineC2Test
{
    private SlotMachine machine;

    /**
     * Prepara la máquina de prueba.
     */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addSymbol(1, "red");
        machine.addSymbol(1, "blue");
        machine.addSymbol(2, "green");
        machine.addSymbol(2, "yellow");
        machine.addSymbol(2, "pink");
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "green");
    }

    /**
     * Debería intercambiar todos los símbolos de dos ruedas.
     */
    @Test
    public void shouldSwapTwoWheels()
    {
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"green", "red"}, machine.configuration());
        assertArrayEquals(new String[] {"green", "yellow", "pink", "red", "blue"}, machine.symbols());
    }

    /**
     * Debería dejar todo igual al intercambiar una rueda consigo misma.
     */
    @Test
    public void shouldKeepEverythingWhenSwappingAWheelWithItself()
    {
        machine.swap(2, 2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", "green"}, machine.configuration());
    }

    /**
     * No debería intercambiar si alguna rueda está bloqueada y debe dejar
     * la configuración igual.
     */
    @Test
    public void shouldNotSwapWhenAWheelIsLocked()
    {
        machine.lock(1);
        machine.swap(1, 2);
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "green"}, machine.configuration());
    }

    /**
     * No debería intercambiar en una máquina sin ruedas.
     */
    @Test
    public void shouldNotSwapInEmptyMachine()
    {
        SlotMachine empty = new SlotMachine();
        empty.swap(1, 2);
        assertFalse(empty.ok());
    }

    /**
     * Debería impedir girar una rueda fijada y permitirlo al soltarla.
     */
    @Test
    public void shouldLockAndUnlockWheel()
    {
        machine.lock(1);
        assertTrue(machine.ok());
        machine.spin(1);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);

        machine.unlock(1);
        machine.spin(1);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    /**
     * Debería saltarse las ruedas fijadas cuando se giran todas, y no
     * dejar cambiar el símbolo de una rueda fijada.
     */
    @Test
    public void shouldIgnoreLockedWheelWhenSpinningAllOrPlacing()
    {
        machine.lock(1);
        machine.spin();
        assertArrayEquals(new String[] {"red", "yellow"}, machine.configuration());
        machine.placeSymbol(1, "blue");
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    /**
     * Debería girar varios pasos de forma cíclica.
     */
    @Test
    public void shouldSpinSeveralSteps()
    {
        machine.spin(2, 2);
        assertTrue(machine.ok());
        assertEquals("pink", machine.configuration()[1]);
        machine.spin(2, 4);
        assertEquals("green", machine.configuration()[1]);
    }

    /**
     * Debería aceptar pasos negativos y pasos enormes.
     */
    @Test
    public void shouldSpinNegativeAndHugeSteps()
    {
        machine.spin(2, -1);
        assertEquals("pink", machine.configuration()[1]);
        machine.spin(2, 1000000000);
        assertEquals("green", machine.configuration()[1]);
    }

    /**
     * No debería cambiar nada con cero pasos ni al girar una rueda
     * fijada con pasos.
     */
    @Test
    public void shouldNotMoveWithZeroStepsOrWhenLocked()
    {
        machine.spin(1, 0);
        assertTrue(machine.ok());
        assertEquals("red", machine.configuration()[0]);
        machine.lock(1);
        machine.spin(1, 1);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    /**
     * Debería dejar la máquina en la configuración dada.
     */
    @Test
    public void shouldSetGivenConfiguration()
    {
        machine.spin(new String[] {"blue", "pink"});
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"blue", "pink"}, machine.configuration());
    }

    /**
     * No debería cambiar ninguna rueda si la configuración dada no es
     * válida (cantidad distinta, color que falta o rueda bloqueada).
     */
    @Test
    public void shouldNotChangeAnythingWithInvalidConfiguration()
    {
        machine.spin(new String[] {"blue"});
        assertFalse(machine.ok());
        machine.spin(new String[] {"blue", "red"});
        assertFalse(machine.ok());
        machine.lock(2);
        machine.spin(new String[] {"blue", "pink"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "green"}, machine.configuration());
    }
}
