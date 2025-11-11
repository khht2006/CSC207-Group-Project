import javax.swing.JFrame;

/**
 * Application entry point for the BikeShare and Walking trip planner.
 */
public class Main {
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addOriginDestinationView()   // enter start+end
                .addCompareView()             // show result comparison
                .addWalkingUseCase()          // compute walking route
                .addBikeUseCase()             // compute walk–bike–walk route
                .addCompareUseCase()          // aggregate + present
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
