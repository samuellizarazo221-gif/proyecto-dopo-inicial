

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
}
