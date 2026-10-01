import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import org.junit.Before;


/**
 * The test class SlotMachineTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class SlotMachineTest
{
    private SlotMachine machine; 
    
    /**
     * Preparamos una maquina con dos ruedas para las pruebas unitarias
     */
    @Before
    public void setUp(){
        machine = new SlotMachine();
        machine.makeInvisible();

        machine.addWheel(0);
        machine.addWheel(1);

        machine.addSymbol(0, "red");
        machine.addSymbol(0, "blue");

        machine.addSymbol(1, "green");
        machine.addSymbol(1, "yellow");

        machine.placeSymbol(0, "red");
        machine.placeSymbol(1, "green");
    }


    /**
     * Debera bloquear una rueda existente y hacer que spin() sobre
     * ella falle, sin cambiar el símbolo que estaba mostrando.
     */
    @Test
    public void shouldLockWheelAndPreventSpinOnIt(){
        machine.lock(0);
        assertTrue(machine.ok());

        machine.spin(0);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    /**
     * No debera permitir bloquear una rueda que no existe.
     */
    @Test
    public void shouldNotLockNonexistentWheel()
    {
        machine.lock(5);
        assertFalse(machine.ok());
    }


    /**
     * Debera al desbloquear una rueda previamente bloqueada, volver
     * a permitir que spin() la gire con normalidad.
     */
    @Test
    public void shouldUnlockWheelAndAllowSpinAgain()
    {
        machine.lock(0);
        machine.unlock(0);
        assertTrue(machine.ok());

        machine.spin(0);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    /**
     * No debera permitir desbloquear una rueda que no existe.
     */
    @Test
    public void shouldNotUnlockNonexistentWheel()
    {
        machine.unlock(5);
        assertFalse(machine.ok());
    }


    /**
     * Debera intercambiar el contenido completo (símbolo actual
     * incluido) entre dos ruedas existentes y no bloqueadas.
     */
    @Test
    public void shouldSwapContentsOfTwoWheels()
    {
        machine.swap(0, 1);
        assertTrue(machine.ok());

        String[] configuration = machine.configuration();
        assertEquals("green", configuration[0]);
        assertEquals("red", configuration[1]);
    }

    /**
     * No debera permitir el swap si alguna de las dos ruedas
     * involucradas está bloqueada; la configuración debe quedar
     * exactamente igual a como estaba.
     */
    @Test
    public void shouldNotSwapWhenEitherWheelIsLocked()
    {
        machine.lock(0);

        machine.swap(0, 1);
        assertFalse(machine.ok());

        String[] configuration = machine.configuration();
        assertEquals("red", configuration[0]);
        assertEquals("green", configuration[1]);
    }
    
    @Test
    public void shouldSetConfigurationWithSpinArray() {
        String[] config = {"blue", "green"};
        machine.spin(config);
        assertTrue(machine.ok());
        assertEquals("blue,green", machine.configuration());
    }

    @Test
    public void shouldFailSpinArrayWithInvalidColor() {
        String[] config = {"yellow", "green"};
        machine.spin(config);
        assertFalse(machine.ok()); // "yellow" no existe en la rueda 0
    }


    // ---------------------------------------------------------------
    // Pruebas que  agregamoss para el resto de metodos de SlotMachine.
   

    /**
     * Debera crear una maquina con 3 ruedas vacias al usar el
     * constructor sin parametros.
     */
    @Test
    public void shouldCreateMachineWithThreeEmptyWheels()
    {
        SlotMachine empty = new SlotMachine();
        empty.makeInvisible();

        assertEquals(3, empty.configuration().length);
        assertEquals(0, empty.symbols().length);
        assertTrue(empty.ok());
    }

    /**
     * Debera crear una maquina con n ruedas, n simbolos por rueda,
     * n colores distintos y sin empezar en jackpot. Si n es menor a 3
     * debera quedarse en 3 ruedas. (Se demora unos segundos porque la
     * maquina nace visible y gira de verdad.)
     */
    @Test
    public void shouldCreateMachineOfGivenSize()
    {
        SlotMachine four = new SlotMachine(4);
        four.makeInvisible();

        assertEquals(4, four.configuration().length);
        assertEquals(16, four.symbols().length);
        assertEquals(4, four.distinctSymbols());
        assertFalse(four.isJackpot());

        SlotMachine tiny = new SlotMachine(1);
        tiny.makeInvisible();
        assertEquals(3, tiny.configuration().length);
    }

    /**
     * Debera agregar una rueda vacia en la posición pedida, corriendo
     * las demás una posición hacia la derecha.
     */
    @Test
    public void shouldAddWheelAtGivenPosition()
    {
        int before = machine.configuration().length;

        machine.addWheel(2);
        assertTrue(machine.ok());

        String[] configuration = machine.configuration();
        assertEquals(before + 1, configuration.length);
        assertNull(configuration[1]);
        assertEquals("green", configuration[2]);
    }

    /**
     * Debera eliminar la rueda pedida y, si la rueda no existe, fallar
     * sin cambiar nada.
     */
    @Test
    public void shouldDeleteWheel()
    {
        int before = machine.configuration().length;

        machine.delWheel(1);
        assertTrue(machine.ok());
        assertEquals(before - 1, machine.configuration().length);
        assertEquals("green", machine.configuration()[0]);

        machine.delWheel(99);
        assertFalse(machine.ok());
        assertEquals(before - 1, machine.configuration().length);
    }

    /**
     * Debera agregar un símbolo a la rueda indicada y fallar si esa
     * rueda no existe.
     */
    @Test
    public void shouldAddSymbolToWheel()
    {
        int before = machine.symbols().length;

        machine.addSymbol(0, "pink");
        assertTrue(machine.ok());
        assertEquals(before + 1, machine.symbols().length);

        machine.addSymbol(99, "pink");
        assertFalse(machine.ok());
        assertEquals(before + 1, machine.symbols().length);
    }

    /**
     * Debera eliminar un color de todas las ruedas donde aparezca y no
     * hacer nada si ese color no existe.
     */
    @Test
    public void shouldDeleteSymbolFromAllWheels()
    {
        int before = machine.symbols().length;

        machine.delSymbols("red");
        assertTrue(machine.ok());
        assertEquals(before - 1, machine.symbols().length);
        assertEquals("blue", machine.configuration()[0]);

        machine.delSymbols("pink");
        assertTrue(machine.ok());
        assertEquals(before - 1, machine.symbols().length);
    }

    /**
     * Debera mostrar en la rueda el color pedido y, si la rueda no
     * tiene ese color o no existe, fallar sin cambiar lo que muestra.
     */
    @Test
    public void shouldPlaceSymbolOnWheel()
    {
        machine.placeSymbol(1, "blue");
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);

        machine.placeSymbol(1, "pink");
        assertFalse(machine.ok());
        assertEquals("blue", machine.configuration()[0]);

        machine.placeSymbol(99, "red");
        assertFalse(machine.ok());
    }

    /**
     * Debera girar una sola rueda al siguiente símbolo (dando la vuelta
     * al llegar al final) y fallar si la rueda no existe.
     */
    @Test
    public void shouldSpinSingleWheel()
    {
        machine.spin(1);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);

        machine.spin(1);
        assertEquals("red", machine.configuration()[0]);

        machine.spin(99);
        assertFalse(machine.ok());
    }

    /**
     * Debera girar todas las ruedas, menos las que están bloqueadas.
     */
    @Test
    public void shouldSpinAllWheelsExceptLockedOnes()
    {
        machine.spin();
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
        assertEquals("yellow", machine.configuration()[1]);

        machine.lock(1);
        machine.spin();
        assertEquals("blue", machine.configuration()[0]);
        assertEquals("green", machine.configuration()[1]);
    }

    /**
     * Debera avanzar una rueda la cantidad de pasos pedida (una vuelta
     * completa la deja igual) y fallar si la rueda no existe.
     */
    @Test
    public void shouldSpinWheelTheGivenSteps()
    {
        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);

        machine.spin(1, 2);
        assertEquals("blue", machine.configuration()[0]);

        machine.spin(99, 1);
        assertFalse(machine.ok());
    }

    /**
     * Debera devolver los colores de todos los símbolos de la máquina,
     * no solo los que se están mostrando.
     */
    @Test
    public void shouldListAllSymbols()
    {
        String[] expected = {"red", "blue", "green", "yellow"};
        assertArrayEquals(expected, machine.symbols());
    }

    /**
     * Debera contar los colores distintos sin contar los repetidos.
     */
    @Test
    public void shouldCountDistinctSymbols()
    {
        assertEquals(4, machine.distinctSymbols());

        machine.addSymbol(1, "red");
        assertEquals(4, machine.distinctSymbols());
    }

    /**
     * Debera devolver el color que muestra cada rueda, y null en las
     * ruedas que no tienen símbolos.
     */
    @Test
    public void shouldShowCurrentConfiguration()
    {
        String[] configuration = machine.configuration();
        assertEquals("red", configuration[0]);
        assertEquals("green", configuration[1]);
        assertNull(configuration[2]);
    }

    /**
     * Debera ser jackpot solo cuando todas las ruedas muestran el mismo
     * color.
     */
    @Test
    public void shouldDetectJackpot()
    {
        assertFalse(machine.isJackpot());

        SlotMachine three = new SlotMachine();
        three.makeInvisible();
        three.addSymbol(0, "red");
        three.addSymbol(1, "red");
        three.addSymbol(2, "red");
        assertTrue(three.isJackpot());

        three.addSymbol(0, "blue");
        three.placeSymbol(1, "blue");
        assertFalse(three.isJackpot());
    }

    /**
     * Debera marcar ok() en true si la última operación salió bien y
     * en false si falló.
     */
    @Test
    public void shouldReportLastOperationResult()
    {
        machine.spin(1);
        assertTrue(machine.ok());

        machine.spin(99);
        assertFalse(machine.ok());

        machine.spin(1);
        assertTrue(machine.ok());
    }

    /**
     * Debera quitar todas las ruedas de la máquina al salir.
     */
    @Test
    public void shouldRemoveAllWheelsOnExit()
    {
        machine.exit();

        assertTrue(machine.ok());
        assertEquals(0, machine.configuration().length);
    }
}