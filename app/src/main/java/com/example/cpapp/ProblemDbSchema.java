package com.example.cpapp;

import java.util.UUID;

public class ProblemDbSchema  {
    public static class ProblemTable{
        public static final String NAME="problems";

        public static final class Cols{
            public static final String UID="uid";
            public static final String ID="id";
            public static final String NAME="name";
            public static final String NOTE="note";
            public static final String URL="url";
            public static final String DESPR="despr";
            public static final String PLATFORM="platform";
            public static final String TYPE ="type";
            public static final String PHOTOCOUNT ="photocount";
        }
    }
    public static class ProblemImages {
        public static final String NAME="problemImages";
        public static final class Cols {
            public static final String UID="uid";
            public static final String FILENAME="fileName";
        }
    }
}

