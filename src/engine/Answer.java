package engine;

import java.util.ArrayList;

public class Answer {
    int[] answer;

    public int[] getAnswer() {
        return answer== null ? new int[]{} : answer;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }
}
