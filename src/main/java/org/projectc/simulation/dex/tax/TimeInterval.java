package org.projectc.simulation.dex.tax;

/**
 * Not used.
 */
public enum TimeInterval {

    ULTRASHORT {
        @Override
        public int interval() {
            return 1;
        }

        @Override
        public double computeWeight(double percentMove) {
            if (percentMove <= 0.2) return 1d;
            if (percentMove <= 0.5) return 1.5d;
            if (percentMove <= 1) return 2.5d;
            if (percentMove <= 2) return 4d;
            if (percentMove <= 5) return 7d;
            if (percentMove <= 10) return 15d;
            return 25d;
        }
    },

    MEDIUM {
        @Override
        public int interval() {
            return 30;
        }

        @Override
        public double computeWeight(double percentMove) {
            if (percentMove <= 1) return 1d;
            if (percentMove <= 5) return 2d;
            if (percentMove <= 10) return 5d;
            if (percentMove <= 30) return 10d;
            return 25d;
        }
    },

    LONG {
        @Override
        public int interval() {
            return 100;
        }

        @Override
        public double computeWeight(double percentMove) {
            if (percentMove <= 2) return 0.1d;
            if (percentMove <= 5) return 0.3d;
            if (percentMove <= 10) return 1d;
            if (percentMove <= 30) return 3d;
            if (percentMove <= 50) return 10d;
            return 25d;
        }
    },
    ;

    public abstract int interval();

    public abstract double computeWeight(double percentMove);
}
