import controllers.ShoppingController;
import views.CLIView;
import services.UserService;

public class Main {
    public static void main(String[] args) {
        CLIView view = new CLIView();
        UserService userService = new UserService();
        ShoppingController controller = new ShoppingController(view, userService);
        controller.start();
    }
}