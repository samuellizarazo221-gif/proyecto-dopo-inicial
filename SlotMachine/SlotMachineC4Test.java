import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del ciclo 4 (tipos de ruedas y de símbolos). Todas
 * se ejecutan con la máquina invisible.
 */
public class SlotMachineC4Test
{
    private SlotMachine machine;

    /**
     * Prepara una máquina vacía e invisible.
     */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
    }

    /**
     * Agrega al final una rueda del tipo dado con símbolos normales.
     *
     * @return la posición (desde 1) de la rueda creada
     */
    private int addWheelWith(String type, String... colors)
    {
        machine.addWheel(type, 99);
        int pos = machine.configuration().length;
        for (String color : colors)
        {
            machine.addSymbol(pos, color);
        }
        return pos;
    }

    /**
     * Debería aceptar los cuatro tipos de rueda, sin importar mayúsculas.
     */
    @Test
    public void shouldAddWheelsOfEveryType()
    {
        String[] types = {"normal", "lefty", "rebel", "reverse", "LEFTY"};
        for (String type : types)
        {
            machine.addWheel(type, 99);
            assertTrue(type, machine.ok());
        }
        assertEquals(types.length, machine.configuration().length);
    }

    /**
     * No debería agregar una rueda de un tipo que no existe.
     */
    @Test
    public void shouldNotAddWheelOfUnknownType()
    {
        machine.addWheel("giant", 1);
        assertFalse(machine.ok());
        machine.addWheel(null, 1);
        assertFalse(machine.ok());
        assertEquals(0, machine.configuration().length);
    }

    /**
     * Debería aceptar los tres tipos de símbolo en una misma rueda.
     */
    @Test
    public void shouldAddSymbolsOfEveryType()
    {
        machine.addWheel(1);
        machine.addSymbol("normal", 1, "red");
        machine.addSymbol("ephemeral", 1, "green");
        machine.addSymbol("shy", 1, "blue");
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", "green", "blue"}, machine.symbols());
    }

    /**
     * No debería agregar un símbolo de tipo desconocido ni repetir un
     * color en la rueda, sin importar el tipo.
     */
    @Test
    public void shouldNotAddUnknownTypeNorRepeatedColor()
    {
        machine.addWheel(1);
        machine.addSymbol("giant", 1, "red");
        assertFalse(machine.ok());
        machine.addSymbol("normal", 1, "red");
        machine.addSymbol("shy", 1, "red");
        assertFalse(machine.ok());
        assertEquals(1, machine.symbols().length);
    }

    /**
     * Una rueda lefty debería copiar a la rueda de su izquierda al girar,
     * sin importar los pasos pedidos.
     */
    @Test
    public void shouldCopyLeftWheelWhenLeftyIsSpun()
    {
        addWheelWith("normal", "red", "green", "blue");
        addWheelWith("lefty", "blue", "red", "green");
        assertArrayEquals(new String[] {"red", "blue"}, machine.configuration());
        machine.spin(1);
        machine.spin(2);
        assertArrayEquals(new String[] {"green", "green"}, machine.configuration());
        assertTrue(machine.isJackpot());
        machine.spin(1, 2);
        machine.spin(2, 1);
        assertArrayEquals(new String[] {"red", "red"}, machine.configuration());
    }

    /**
     * Al girar todas las ruedas de izquierda a derecha, la lefty debería
     * quedar igual a su vecina ya girada.
     */
    @Test
    public void shouldCopyAlreadySpunNeighbourWhenSpinningAll()
    {
        addWheelWith("normal", "red", "green", "blue");
        addWheelWith("lefty", "blue", "red", "green");
        machine.spin();
        assertArrayEquals(new String[] {"green", "green"}, machine.configuration());
    }

    /**
     * Una lefty sin rueda a su izquierda, o sin el símbolo que muestra su
     * vecina, no debería copiar a nadie: gira como una rueda normal.
     */
    @Test
    public void shouldSpinLeftyNormallyWhenThereIsNothingToCopy()
    {
        addWheelWith("lefty", "red", "green");
        machine.spin(1, 1);
        assertEquals("green", machine.configuration()[0]);

        machine = new SlotMachine();
        machine.makeInvisible();
        addWheelWith("normal", "red", "green");
        addWheelWith("lefty", "blue", "pink");
        machine.spin(1, 1);
        machine.spin(2, 1);
        assertArrayEquals(new String[] {"green", "pink"}, machine.configuration());
    }

    /**
     * Al eliminar la rueda que estaba a su izquierda, la lefty debería
     * copiar a su nueva vecina.
     */
    @Test
    public void shouldCopyNewLeftNeighbourAfterDeletingWheel()
    {
        addWheelWith("normal", "red", "green");
        addWheelWith("normal", "red", "green");
        addWheelWith("lefty", "red", "green");
        machine.delWheel(2);
        assertTrue(machine.ok());
        machine.spin(1, 1);
        machine.spin(2, 1);
        assertArrayEquals(new String[] {"green", "green"}, machine.configuration());
    }

    /**
     * Una rueda rebel no debería dejarse bloquear, intercambiar ni
     * eliminar, y todo debería quedar como estaba.
     */
    @Test
    public void shouldNotLockSwapNorDeleteRebelWheel()
    {
        addWheelWith("normal", "red", "green");
        addWheelWith("rebel", "blue", "pink");
        machine.lock(2);
        assertFalse(machine.ok());
        machine.swap(1, 2);
        assertFalse(machine.ok());
        machine.swap(2, 1);
        assertFalse(machine.ok());
        machine.delWheel(2);
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "blue"}, machine.configuration());
        assertEquals(4, machine.symbols().length);
    }

    /**
     * Una rueda rebel sí debería girar y mostrar el símbolo pedido, y
     * las ruedas normales a su lado deberían seguir operándose igual.
     */
    @Test
    public void shouldLetRebelWheelSpinAndKeepOthersWorking()
    {
        addWheelWith("normal", "red", "green");
        addWheelWith("rebel", "blue", "pink");
        machine.spin(2, 1);
        assertTrue(machine.ok());
        assertEquals("pink", machine.configuration()[1]);
        machine.placeSymbol(2, "blue");
        assertTrue(machine.ok());
        machine.lock(1);
        assertTrue(machine.ok());
        machine.unlock(1);
        machine.delWheel(1);
        assertTrue(machine.ok());
        assertEquals(1, machine.configuration().length);
    }

    /**
     * Una rueda reverse debería girar al contrario que las demás.
     */
    @Test
    public void shouldSpinReverseWheelBackwards()
    {
        addWheelWith("reverse", "red", "green", "blue");
        machine.spin(1);
        assertEquals("blue", machine.configuration()[0]);
        machine.spin(1, 2);
        assertEquals("red", machine.configuration()[0]);
        machine.spin(1, -1);
        assertEquals("green", machine.configuration()[0]);
    }

    /**
     * Un símbolo shy debería alternar entre visible e invisible cada vez
     * que queda seleccionado; invisible no cuenta en la configuración.
     */
    @Test
    public void shouldAlternateShySymbolEachTimeItIsSelected()
    {
        machine.addWheel(1);
        machine.addSymbol(1, "red");
        machine.addSymbol("shy", 1, "blue");
        machine.spin(1);
        assertNull(machine.configuration()[0]);
        assertEquals(0, machine.distinctSymbols());
        machine.spin(1);
        assertEquals("red", machine.configuration()[0]);
        machine.spin(1);
        assertEquals("blue", machine.configuration()[0]);
        assertArrayEquals(new String[] {"red", "blue"}, machine.symbols());
    }

    /**
     * No debería haber jackpot mientras un símbolo shy esté invisible.
     */
    @Test
    public void shouldNotBeJackpotWhileShySymbolIsHidden()
    {
        addWheelWith("normal", "red");
        machine.addWheel(2);
        machine.addSymbol("normal", 2, "green");
        machine.addSymbol("shy", 2, "red");
        assertFalse(machine.isJackpot());
        machine.spin(2, 1);
        assertNull(machine.configuration()[1]);
        assertFalse(machine.isJackpot());
        machine.spin(2, 2);
        assertEquals("red", machine.configuration()[1]);
        assertTrue(machine.isJackpot());
    }

    /**
     * Un símbolo efímero debería seguir siendo seleccionable aunque la
     * rueda gire muchas veces (queda reducido a un punto, no desaparece).
     */
    @Test
    public void shouldKeepEphemeralSymbolSelectableAfterManySpins()
    {
        machine.addWheel(1);
        machine.addSymbol("ephemeral", 1, "red");
        machine.addSymbol("normal", 1, "green");
        for (int i = 0; i < 25; i++)
        {
            machine.spin(1);
        }
        assertTrue(machine.ok());
        machine.placeSymbol(1, "red");
        assertTrue(machine.ok());
        assertEquals("red", machine.configuration()[0]);
        assertEquals(2, machine.symbols().length);
    }

    /**
     * Debería poder girar a la vez una máquina con todos los tipos de
     * rueda y de símbolo.
     */
    @Test
    public void shouldSpinMachineWithEveryType()
    {
        String[] wheelTypes = {"normal", "lefty", "rebel", "reverse"};
        for (String type : wheelTypes)
        {
            int pos = addWheelWith(type, "red");
            machine.addSymbol("ephemeral", pos, "green");
            machine.addSymbol("shy", pos, "blue");
        }
        for (int i = 0; i < 12; i++)
        {
            machine.spin();
            assertTrue(machine.ok());
        }
        assertEquals(4, machine.configuration().length);
    }
}
