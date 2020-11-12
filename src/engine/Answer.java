package engine;

public class Answer {
    private boolean success;
    private String feedback;

    public Answer(boolean isRight) {
        if (isRight) {
            success = true;
            feedback = "Congratulations, you're right!";
        } else {
            success = false;
            feedback = "Wrong answer! Please, try again.";
        }
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean getSuccess() {
        return success;
    }
}
