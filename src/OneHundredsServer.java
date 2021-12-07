import java.io.*;
import java.net.*;
import java.util.*;

public class OneHundredsServer {
    public static void main(String[] args) throws IOException {
//        String guess = "Hello";
        int portNumber = 8010;
        String player1 = "";
        int incorrectCount = 0;
        String underscores = "_"; // variable that holds underscores to represent letters in the word

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        ){
            String inputLine, outputLine;

            outputLine = "Welcome to One Hundreds! Please enter your name:";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.matches("[a-zA-z]{4,10}")) {
                    player1 = inputLine;
                    outputLine = "Good";
                    out.println(outputLine);
                    break;
                } else {
                    outputLine = "Sorry. Name must be between 4-10 characters. Try again. Please enter your name: ";
                    out.println(outputLine);
                }
            }
            System.out.println("Leaving initial loop");

//            Scanner inputDevice = new Scanner(System.in);
//            System.out.println("=== One Hundreds ===");

//            int numberOfPlayers = getNumberOfPlayers(inputDevice);
            int numberOfPlayers = 4;

            ArrayList<Player> players = new ArrayList<>();
            getPlayerNames(player1, numberOfPlayers, players);

            Map<String, Integer> scores = new HashMap<>();
            for(Player player : players){
                scores.put(player.getName(), 0);
            }

            CardDeck cardDeck = new CardDeck();

            ArrayList<Card> deck = cardDeck.GenerateDeck();

//            cardDeck.ShuffleDeck(deck, getNumberOfShuffles(inputDevice));
            cardDeck.ShuffleDeck(deck, 5);

            objectOutput.writeObject(players.get(0));
            int numberOfRoundsToPlay = deck.size() / players.size();

            dealCards(cardDeck, deck, players, objectInput, objectOutput);
            playOneHundreds(numberOfRoundsToPlay, players, scores, in, out, objectInput, objectOutput);

            //System.out.println("=== End of Game ===");
            outputLine = "=== End of Game ===";
            out.println(outputLine);

            int winningScore = Collections.max(scores.values());

            //System.out.println("Scores: ");
            outputLine = "Scores: ";
            out.println(outputLine);
            ArrayList<String> winners = new ArrayList<>();

            displayScores(scores, out);

            determineWinners(winningScore, scores, winners);

            displayWinners(scores, winners, out);

            displayRemainingCards(cardDeck, deck, out);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }

    public static int getNumberOfPlayers(Scanner inputDevice){
        int numberOfPlayers;
        while(true){
            try {
                System.out.println("Number of players (2-4): ");
                numberOfPlayers = inputDevice.nextInt();
                if ((numberOfPlayers < 2) || (numberOfPlayers > 4)){
                    System.out.println("Number of players must be between 2 and 4\n");
                    continue;
                }
                break;
            } catch (InputMismatchException e){
                System.out.println("Integer input required. Try again! \n");
                inputDevice.nextLine();
            }
        }
        inputDevice.nextLine();
        System.out.println();
        return numberOfPlayers;
    }

    public static void getPlayerNames(String player1, int numberOfPlayers, ArrayList<Player> players){
        String[] playerNames = {player1, "John", "Dan", "Alex"};

        for(int i = 0; i < numberOfPlayers; i++){
            LinkedList<Card> hand = new LinkedList<>();
//            System.out.println("Player " + (i+1) + "'s Name: ");
            String playerName = playerNames[i];
            Player player = new Player(playerName, hand);
            players.add(player);
        }

//        for(int i = 0; i < numberOfPlayers; i++){
//            LinkedList<Card> hand = new LinkedList<>();
//            System.out.println("Player " + (i+1) + "'s Name: ");
//            String playerName = inputDevice.nextLine();
//            Player player = new Player(playerName, hand);
//            players.add(player);
//        }
    }

    public static int getNumberOfShuffles(Scanner inputDevice){
        int numberOfShuffles;
        while(true){
            try {
                System.out.println("\nNumber of Shuffles: ");
                numberOfShuffles = inputDevice.nextInt();
                break;
            } catch (InputMismatchException e){
                System.out.println("Integer input required. Try again! \n");
                inputDevice.nextLine();
            }
        }
        inputDevice.nextLine();
        return numberOfShuffles;
    }

    public static void dealCards(CardDeck cardDeck, ArrayList<Card> deck, ArrayList<Player> players,
                                 ObjectInputStream objectInput, ObjectOutputStream objectOutput) throws IOException {
        while ((cardDeck.CardsRemaining(deck) - players.size()) >= 0) {
            int count = 0;
            for (Player player : players) {
                if (count == 0){
                    objectOutput.writeObject(deck.get(0));
                } else {
                    System.out.println(count + ". Card Value: " + deck.get(0).getValue() + " " + deck.get(0).getStatus());
                    LinkedList<Card> playerHand = player.getHand();
                    playerHand.add(deck.get(0));
                }
                deck.remove(0);
                count++;
            }
        }
        objectOutput.writeObject(null);
    }

    public static void playOneHundreds(int numberOfRoundsToPlay, ArrayList<Player> players, Map<String, Integer> scores,
                                       BufferedReader in, PrintWriter out, ObjectInputStream inputObject,
                                       ObjectOutputStream objectOutput) throws IOException, ClassNotFoundException {
        ArrayList<Card> cardsPlayedInRound = new ArrayList<>();
        List<Card> wildCardsInRound = new ArrayList<>();
        int roundNumber = 1;
        String inputLine, outputLine;
        outputLine = "Press any key to begin!";
        out.println(outputLine);
        while ((roundNumber <= numberOfRoundsToPlay) && (in.readLine() != null)) {
//                System.out.println("Round #" + roundNumber);
            outputLine = "Round #" + roundNumber;
            out.println(outputLine);
            Card lowestValuedWildCard = null;

            int count = 0;
            for (Player player : players) {
                if (count == 0) {
//                    cardsPlayedInRound.add(player.getHand().get(0));
                    Card playerCard = null;
                    while ((playerCard = (Card) inputObject.readObject()) != null) {
                        cardsPlayedInRound.add(playerCard);
                        outputLine = player.getName() + ": " + playerCard.getValue() + " " +
                                playerCard.getStatus();
                        out.println(outputLine);
                    }
                } else {
                    cardsPlayedInRound.add(player.getHand().get(0));
                    outputLine = player.getName() + ": " + player.getHand().get(0).getValue() + " " +
                            player.getHand().get(0).getStatus();
                    out.println(outputLine);
                    //System.out.println(player.getName() + ": " + player.getHand().get(0).getValue() + " " + player.getHand().get(0).getStatus());
                    player.getHand().remove(0);
                }
                count++;
            }

            for (Card card : cardsPlayedInRound) {
                if (card.getStatus().equals("w")) {
                    wildCardsInRound.add(card);
                }
            }

            if (wildCardsInRound.size() > 0) {
                lowestValuedWildCard = wildCardsInRound.get(0);
                if (wildCardsInRound.size() > 1) {
                    for (Card wildCard : wildCardsInRound) {
                        if (wildCard.getValue() < lowestValuedWildCard.getValue()) {
                            lowestValuedWildCard.setValue(wildCard.getValue());
                        }
                    }
                }
            }

            int winningPlayerIndex = 0;
            if (lowestValuedWildCard == null) {
                Card highestCardInRound = cardsPlayedInRound.get(0);
                for (Card card : cardsPlayedInRound) {
                    if (card.getValue() > highestCardInRound.getValue()) {
                        highestCardInRound = card;
                    }
                }
                winningPlayerIndex = cardsPlayedInRound.indexOf(highestCardInRound);
            } else {
                winningPlayerIndex = cardsPlayedInRound.indexOf(lowestValuedWildCard);
            }

            String winningPlayerName = players.get(winningPlayerIndex).getName();
            scores.put(winningPlayerName, scores.get(winningPlayerName) + 1);
            outputLine = winningPlayerName + " has won the hand\n";
            out.println(outputLine);
            //System.out.println(winningPlayerName + " has won the hand\n");
            roundNumber++;
            // empty lists for next hand/round
            cardsPlayedInRound.clear();
            wildCardsInRound.clear();
            outputLine = "Press any key to continue!";
            out.println(outputLine);
            if (roundNumber > numberOfRoundsToPlay) {
                while (in.readLine() != null) {
                    break;
                }
                break;
            }
        }
    }

    public static void displayScores(Map<String, Integer> scores, PrintWriter out){
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            //System.out.println(entry.getKey() + " - " + entry.getValue());
            String outputLine = entry.getKey() + " - " + entry.getValue();
            out.println(outputLine);
        }
    }

    public static void determineWinners(int winningScore, Map<String, Integer> scores, ArrayList<String> winners){
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue()==winningScore) {
                winners.add(entry.getKey());
            }
        }
    }

    public static void displayWinners(Map<String, Integer> scores, ArrayList<String> winners, PrintWriter out){
        if (winners.size() > 1){
            //System.out.println("\nWinners: ");
            String outputLine = "\nWinners: ";
            out.println(outputLine);
            int counter = 0;
            for (String winner: winners) {
                if (counter != winners.size() - 1) {
                    //System.out.print(winner + ", ");
                    outputLine += winner + ", ";
                } else {
                    //System.out.print(winner + "\n");
                    outputLine += winner;
                }
                counter++;
            }
            out.println(outputLine);
        } else {
            String outputLine = "\nWinner: " + winners.get(0);
            out.println(outputLine);
            //System.out.println("\nWinner: " + winners.get(0));
        }

        String outputLine = "Score: " + scores.get(winners.get(0));
        out.println(outputLine);
        //System.out.println("Score: " + scores.get(winners.get(0)));
    }

    public static void displayRemainingCards(CardDeck cardDeck, ArrayList<Card> deck, PrintWriter out){
        //System.out.println("\nCards remaining in deck: ");
        String outputLine = "\nCards remaining in deck: ";
        out.println(outputLine);
        if(cardDeck.CardsRemaining(deck) > 0) {
            cardDeck.PrintDeck(deck, out);
        } else {
            //System.out.println("None");
            outputLine = "None";
            out.println(outputLine);
        }
    }
}
