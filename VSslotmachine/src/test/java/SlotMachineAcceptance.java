/**
 * Pruebas de aceptación del simulador. Se ejecutan con el botón Run de
 * VS Code (método main) y muestran la máquina visible, paso a paso.
 * No son pruebas JUnit: abren ventana, así que no se mezclan con las de
 * unidad (que deben ser invisibles).
 *
 * Escenario 1: armar una máquina, girar, intercambiar, bloquear y ganar.
 * Escenario 2: solucionar y simular el problema de la maratón.
 * Escenario 3: tipos de ruedas y de símbolos.
 */
public class SlotMachineAcceptance
{
    private static final int PAUSE = 1500;

    /**
     * Ejecuta los tres escenarios en orden.
     *
     * @param args no se usa
     */
    public static void main(String[] args)
    {
        scenarioOne();
        scenarioTwo();
        scenarioThree();
        System.out.println("Aceptación terminada.");
        System.exit(0);
    }

    /**
     * Escenario 1: ciclos 1 y 2. La máquina cambia de aspecto al ganar.
     */
    private static void scenarioOne()
    {
        System.out.println("Escenario 1: máquina manual");
        SlotMachine machine = new SlotMachine();
        String[] colors = {"red", "green", "blue", "gold"};
        for (int wheel = 1; wheel <= 3; wheel++)
        {
            machine.addWheel(wheel);
            for (String color : colors)
            {
                machine.addSymbol(wheel, color);
            }
        }
        machine.makeVisible();
        pause();
        machine.spin();
        pause();
        machine.swap(1, 3);
        pause();
        machine.lock(2);
        machine.spin(1, 2);
        pause();
        machine.unlock(2);
        machine.spin(new String[] {"red", "red", "red"});
        System.out.println("¿Ganó? " + machine.isJackpot() + " (la máquina se ve dorada)");
        pause();
        machine.exit();
    }

    /**
     * Escenario 2: ciclo 3. Simula la solución de una máquina aleatoria.
     */
    private static void scenarioTwo()
    {
        System.out.println("Escenario 2: simulate(5)");
        SlotMachineContest.simulate(5);
        pause();
    }

    /**
     * Escenario 3: ciclo 4. Cada tipo se distingue visualmente.
     */
    private static void scenarioThree()
    {
        System.out.println("Escenario 3: tipos de rueda y de símbolo");
        SlotMachine machine = new SlotMachine();
        String[] wheels = {"normal", "lefty", "rebel", "reverse"};
        for (int i = 0; i < wheels.length; i++)
        {
            machine.addWheel(wheels[i], i + 1);
            machine.addSymbol("normal", i + 1, "red");
            machine.addSymbol("ephemeral", i + 1, "green");
            machine.addSymbol("shy", i + 1, "blue");
            machine.addSymbol("normal", i + 1, "gold");
        }
        machine.makeVisible();
        pause();
        for (int turn = 0; turn < 6; turn++)
        {
            machine.spin();
            pause();
        }
        machine.exit();
    }

    /**
     * Espera para que se pueda ver cada paso.
     */
    private static void pause()
    {
        try
        {
            Thread.sleep(PAUSE);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
