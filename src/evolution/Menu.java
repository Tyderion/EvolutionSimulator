package evolution;

public enum Menu {
    START_SCREEN(0),
    MAIN_SCREEN(1),
    CREATE_CREATURES(2),
    VIEW_GENERATED(3),
    START_SIMULATION(4),
    SIMULATION_RUNNING(5),
    CLEANUP_SIMULATION(6),
    SIMULATION_RESULT(7),
    SIMULATION_SORT_VIS(8),
    SIMULATION_RESULT_SORTED(9),
    KILL_500(10),
    KILLS_VISUALIZE(11),
    REPRODUCE(12),
    SHOW_NEW_CREATURES(13);

    public final int value;

    Menu(int value) {
        this.value = value;
    }
}
