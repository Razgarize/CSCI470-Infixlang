       IDENTIFICATION DIVISION.
       PROGRAM-ID. GUESSING.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 GUESS          PIC 999.
       01 PREVIOUS-GUESS PIC 999.
       01 GUESS-COUNT    PIC 999 VALUE 0.
       01 SECRET         PIC 999.
       01 UNIX-TIME      PIC 9(9).
       01 MSG-RESP       PIC X(8).

       SCREEN SECTION.
       01 GUESSING-BOARD.
           05 BLANK SCREEN.
           05 LINE 2 COLUMN 21 VALUE "GUESSING GAME".
           05 LINE 6 COLUMN 1  VALUE "Previous Guess:".
           05 LINE 6 COLUMN 17 PIC Z(3) USING PREVIOUS-GUESS.
           05 LINE 6 COLUMN 32 VALUE "Guess Count:".
           05 LINE 6 COLUMN 45 PIC Z(3) USING GUESS-COUNT.
           05 LINE 7 COLUMN 17 PIC X(8) USING MSG-RESP.
       01 GUESSING-INPUT.
           05 LINE 4 COLUMN 10 VALUE "Guess:".
           05 LINE 4 COLUMN 17 PIC Z(3) USING GUESS.

       PROCEDURE DIVISION.
           CALL "time" USING BY REFERENCE UNIX-TIME.
           CALL "srand" USING UNIX-TIME.
           CALL "rand" RETURNING SECRET.

           PERFORM USER-GUESS UNTIL GUESS = SECRET.
           DISPLAY GUESSING-BOARD.
           DISPLAY GUESSING-INPUT.

       USER-GUESS.
           DISPLAY GUESSING-BOARD.
           ACCEPT GUESSING-INPUT.
           MOVE GUESS TO PREVIOUS-GUESS.
           ADD 1 TO GUESS-COUNT.
           IF GUESS < SECRET
               MOVE "TOO LOW" TO MSG-RESP.
           IF GUESS > SECRET
               MOVE "TOO HIGH" TO MSG-RESP.
           IF GUESS = SECRET
               MOVE "CORRECT" TO MSG-RESP.
