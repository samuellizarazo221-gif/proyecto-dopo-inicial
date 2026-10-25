import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad compartidas del ciclo 2 (creación colectiva). El
 * nombre de cada prueba incluye las iniciales de sus autores.
 */
public class SlotMachineCC2Test
{
    private SlotMachine machine;

    /**
     * Prepara una máquina de dos ruedas con tres símbolos cada una.
     */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
        machine.addWheel(1);
        machine.addWheel(2);
        String[] colors = {"red", "green", "blue"};
        for (String color : colors)
        {
            machine.addSymbol(1, color);
            machine.addSymbol(2, color);
        }
    }

    /**
     * Debería conservar todos los símbolos al intercambiar dos ruedas.
     */
    @Test
    public void accordingBoLiShouldKeepAllSymbolsWhenSwapping()
    {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertEquals(6, machine.symbols().length);
        assertEquals(2, machine.distinctSymbols());
        assertArrayEquals(new String[] {"blue", "red"}, machine.configuration());
    }

    /**
     * No debería girar con pasos una rueda fijada, aunque se haya
     * soltado otra.
     */
    @Test
    public void accordingBoLiShouldNotSpinLockedWheelWithSteps()
    {
        machine.lock(1);
        machine.lock(2);
        machine.unlock(2);
        machine.spin(1, 2);
        assertFalse(machine.ok());
        machine.spin(2, 2);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[1]);
    }
}
