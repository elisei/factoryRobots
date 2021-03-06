package inf112.app.objects;


import inf112.app.map.Position;

import java.util.Objects;

public class Flag implements IBoardElement {
    private int num;

    public Flag(int num){
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flag)) return false;
        Flag flag = (Flag) o;
        return getNum() == flag.getNum();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNum());
    }

    @Override
    public void doAction(Robot robot) {
        Flag lastVisitedFlag = robot.getVisitedFlag();
        if(lastVisitedFlag == null){
            if(num == 1){
                robot.setVisitedFlag(this);
                Position pos = robot.getPos();
                robot.setCheckPoint(pos);
            }
        } else {
            int lastVisitedFlagNum = robot.getVisitedFlag().getNum();
            if(lastVisitedFlagNum == num-1){
                robot.setVisitedFlag(this);
                Position pos = robot.getPos();
                robot.setCheckPoint(pos);
            }
        }
    }
}
