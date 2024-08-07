import mvc.*;
public class BattleWar {

    public static void main(String[] args) {
        View view = new View();
        Model model = new Model();
        Controller controller = new Controller(view, model);
        view.setController(controller);
        view.setModel(model);
        view.init();
    }
}