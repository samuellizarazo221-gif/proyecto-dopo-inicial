import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del ciclo 1 de SlotMachine (siempre en modo
 * invisible). La máquina de prueba tiene dos ruedas: la 1 con los
 * símbolos red y blue (muestra red) y la 2 con green y yellow (muestra
 * green).
 */
public class SlotMachineTest
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
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "green");
    }

    /**
     * Debería crear una máquina sin ruedas.
     */
    @Test
    public void shouldCreateEmptyMachine()
    {
        SlotMachine empty = new SlotMachine();
        assertEquals(0, empty.configuration().length);
        assertTrue(empty.ok());
    }

    /**
     * Debería insertar la rueda en la posición pedida, empezando en 1.
     */
    @Test
    public void shouldInsertWheelAtGivenPosition()
    {
        machine.addWheel(2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"red", null, "green"}, machine.configuration());
    }

    /**
     * Debería usar la primera posición si la posición es menor a 1 y la
     * última si es mayor que la cantidad de ruedas.
     */
    @Test
    public void shouldAdjustWheelPositionToValidRange()
    {
        machine.addWheel(0);
        machine.addWheel(99);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {null, "red", "green", null}, machine.configuration());
    }

    /**
     * Debería eliminar una rueda.
     */
    @Test
    public void shouldDeleteWheel()
    {
        machine.delWheel(1);
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"green"}, machine.configuration());
    }

    /**
     * No debería eliminar una rueda si la máquina no tiene ruedas.
     */
    @Test
    public void shouldNotDeleteWheelFromEmptyMachine()
    {
        SlotMachine empty = new SlotMachine();
        empty.delWheel(1);
        assertFalse(empty.ok());
    }

    /**
     * Debería agregar un símbolo al final de la rueda indicada.
     */
    @Test
    public void shouldAddSymbolToWheel()
    {
        machine.addSymbol(1, "pink");
        assertTrue(machine.ok());
        assertEquals(5, machine.symbols().length);
        assertEquals("pink", machine.symbols()[2]);
    }

    /**
     * Debería usar la última rueda si la posición pasa del máximo.
     */
    @Test
    public void shouldAddSymbolToLastWheelWhenPositionIsTooBig()
    {
        machine.addSymbol(99, "pink");
        assertTrue(machine.ok());
        machine.placeSymbol(2, "pink");
        assertEquals("pink", machine.configuration()[1]);
    }

    /**
     * No debería aceptar un color que no es CSS ni una rueda repetida.
     */
    @Test
    public void shouldNotAddInvalidOrRepeatedSymbol()
    {
        machine.addSymbol(1, "notacolor");
        assertFalse(machine.ok());
        machine.addSymbol(1, "red");
        assertFalse(machine.ok());
        assertEquals(4, machine.symbols().length);
    }

    /**
     * Debería eliminar el símbolo de todas las ruedas que lo tengan y
     * mostrar el siguiente si era el que se veía.
     */
    @Test
    public void shouldDeleteSymbolFromEveryWheel()
    {
        machine.addSymbol(2, "red");
        machine.delSymbol("red");
        assertTrue(machine.ok());
        assertEquals(3, machine.symbols().length);
        assertEquals("blue", machine.configuration()[0]);
    }

    /**
     * No debería eliminar un símbolo que ninguna rueda tiene.
     */
    @Test
    public void shouldNotDeleteMissingSymbol()
    {
        machine.delSymbol("pink");
        assertFalse(machine.ok());
        assertEquals(4, machine.symbols().length);
    }

    /**
     * Debería mostrar el símbolo pedido en la rueda.
     */
    @Test
    public void shouldPlaceSymbol()
    {
        machine.placeSymbol(1, "blue");
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    /**
     * No debería mostrar un símbolo que la rueda no tiene.
     */
    @Test
    public void shouldNotPlaceSymbolTheWheelDoesNotHave()
    {
        machine.placeSymbol(1, "green");
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    /**
     * Debería girar una rueda al siguiente símbolo, de forma cíclica.
     */
    @Test
    public void shouldSpinOneWheelCyclically()
    {
        machine.spin(1);
        assertEquals("blue", machine.configuration()[0]);
        machine.spin(1);
        assertEquals("red", machine.configuration()[0]);
        assertEquals("green", machine.configuration()[1]);
    }

    /**
     * Debería girar todas las ruedas.
     */
    @Test
    public void shouldSpinAllWheels()
    {
        machine.spin();
        assertArrayEquals(new String[] {"blue", "yellow"}, machine.configuration());
    }

    /**
     * Debería listar los colores de todos los símbolos, rueda por rueda.
     */
    @Test
    public void shouldListAllSymbols()
    {
        assertArrayEquals(new String[] {"red", "blue", "green", "yellow"}, machine.symbols());
    }

    /**
     * Debería contar solo los símbolos distintos que se están viendo.
     */
    @Test
    public void shouldCountDistinctVisibleSymbols()
    {
        assertEquals(2, machine.distinctSymbols());
        machine.addSymbol(2, "red");
        machine.placeSymbol(2, "red");
        assertEquals(1, machine.distinctSymbols());
    }

    /**
     * Debería detectar la configuración ganadora solo cuando todas las
     * ruedas muestran el mismo símbolo.
     */
    @Test
    public void shouldDetectJackpotOnlyWhenAllWheelsMatch()
    {
        assertFalse(machine.isJackpot());
        machine.addSymbol(2, "red");
        machine.placeSymbol(2, "red");
        assertTrue(machine.isJackpot());
        machine.spin(2);
        assertFalse(machine.isJackpot());
    }

    /**
     * Debería poder hacerse visible e invisible sin perder el estado de
     * la última operación.
     */
    @Test
    public void shouldKeepWorkingWhenSetInvisible()
    {
        machine.makeInvisible();
        assertTrue(machine.ok());
        machine.spin(1);
        assertEquals("blue", machine.configuration()[0]);
    }

    /**
     * Debería terminar el simulador dejando la máquina sin ruedas.
     */
    @Test
    public void shouldExitSimulator()
    {
        machine.exit();
        assertTrue(machine.ok());
        assertEquals(0, machine.configuration().length);
    }
}
