package dat066.dat066_projekt;

public class Goals {

    private String Name;
    private String Type;
    private String EndGoal;
    private String Time;
    private String GoalProgres;

    public Goals(){
    }



    public Goals(String name, String type, String endGoal, String time, String goalProgres){
        Name = name;
        Type = type;
        EndGoal = endGoal;
        Time = time;
        GoalProgres = goalProgres;

    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public String getEndGoal() {
        return EndGoal;
    }

    public String getTime() {
        return Time;
    }

    public String getGoalProgres() {
        return GoalProgres;
    }
}
