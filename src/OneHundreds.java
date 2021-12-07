import java.util.*;

public class OneHundreds {
    public static void main(String[] args) {
        Scanner inputDevice = new Scanner(System.in);
        System.out.println("=== One Hundreds ===");

        int numberOfPlayers = getNumberOfPlayers(inputDevice);

        ArrayList<Player> players = new ArrayList<>();
        getPlayerNames(inputDevice, numberOfPlayers, players);

        Map<String, Integer> scores = new HashMap<>();
        for(Player player : players){
            scores.put(player.getName(), 0);
        }

        CardDeck cardDeck = new CardDeck();

        ArrayList<Card> deck = cardDeck.GenerateDeck();

        cardDeck.ShuffleDeck(deck, getNumberOfShuffles(inputDevice));

        int numberOfRoundsToPlay = deck.size() / players.size();

        dealCards(cardDeck, deck, players);
        playOneHundreds(numberOfRoundsToPlay, players, scores);

        System.out.println("=== End of Game ===");
        int winningScore = Collections.max(scores.values());

        System.out.println("Scores: ");
        ArrayList<String> winners = new ArrayList<>();

        displayScores(scores);

        determineWinners(winningScore, scores, winners);

        displayWinners(scores, winners);

        displayRemainingCards(cardDeck, deck);
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

    public static void getPlayerNames(Scanner inputDevice, int numberOfPlayers, ArrayList<Player> players){
        for(int i = 0; i < numberOfPlayers; i++){
            LinkedList<Card> hand = new LinkedList<>();
            System.out.println("Player " + (i+1) + "'s Name: ");
            String playerName = inputDevice.nextLine();
            Player player = new Player(playerName, hand);
            players.add(player);
        }
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

    public static void dealCards(CardDeck cardDeck, ArrayList<Card> deck, ArrayList<Player> players) {
        while ((cardDeck.CardsRemaining(deck) - players.size()) >= 0) {
            for (Player player : players) {
                LinkedList<Card> playerHand = player.getHand();
                playerHand.add(deck.get(0));
                deck.remove(0);
            }
        }
    }

    public static void playOneHundreds(int numberOfRoundsToPlay, ArrayList<Player> players, Map<String, Integer> scores){
        ArrayList<Card> cardsPlayedInRound = new ArrayList<>();
        List<Card> wildCardsInRound = new ArrayList<>();
        int roundNumber = 1;
        while (roundNumber <= numberOfRoundsToPlay){
            System.out.println("Round #" + roundNumber);
            Card lowestValuedWildCard = null;

            for (Player player : players) {
                cardsPlayedInRound.add(player.getHand().get(0));
                System.out.println(player.getName() + ": " + player.getHand().get(0).getValue() + " " + player.getHand().get(0).getStatus());
                player.getHand().remove(0);
            }

            for (Card card : cardsPlayedInRound){
                if(card.getStatus().equals("w")){
                    wildCardsInRound.add(card);
                }
            }

            if (wildCardsInRound.size() > 0) {
                lowestValuedWildCard = wildCardsInRound.get(0);
                if (wildCardsInRound.size() > 1) {
                    for (Card wildCard : wildCardsInRound) {
                        if (wildCard.getValue() < lowestValuedWildCard.getValue()){
                            lowestValuedWildCard.setValue(wildCard.getValue());
                        }
                    }
                }
            }

            int winningPlayerIndex = 0;
            if(lowestValuedWildCard == null){
                Card highestCardInRound = cardsPlayedInRound.get(0);
                for (Card card : cardsPlayedInRound){
                    if(card.getValue() > highestCardInRound.getValue()){
                        highestCardInRound = card;
                    }
                }
                winningPlayerIndex = cardsPlayedInRound.indexOf(highestCardInRound);
            } else {
                winningPlayerIndex = cardsPlayedInRound.indexOf(lowestValuedWildCard);
            }

            String winningPlayerName = players.get(winningPlayerIndex).getName();
            scores.put(winningPlayerName, scores.get(winningPlayerName) + 1);
            System.out.println(winningPlayerName + " has won the hand\n");
            roundNumber++;
            // empty lists for next hand/round
            cardsPlayedInRound.clear();
            wildCardsInRound.clear();
        }
    }

    public static void displayScores(Map<String, Integer> scores){
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    public static void determineWinners(int winningScore, Map<String, Integer> scores, ArrayList<String> winners){
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue()==winningScore) {
                winners.add(entry.getKey());
            }
        }
    }

    public static void displayWinners(Map<String, Integer> scores, ArrayList<String> winners){
        if (winners.size() > 1){
            System.out.println("\nWinners: ");
            int counter = 0;
            for (String winner: winners) {
                if (counter != winners.size() - 1) {
                    System.out.print(winner + ", ");
                } else {
                    System.out.print(winner + "\n");
                }
                counter++;
            }
        } else {
            System.out.println("\nWinner: " + winners.get(0));
        }

        System.out.println("Score: " + scores.get(winners.get(0)));
    }

    public static void displayRemainingCards(CardDeck cardDeck, ArrayList<Card> deck){
        System.out.println("\nCards remaining in deck: ");

        if(cardDeck.CardsRemaining(deck) > 0) {
            //cardDeck.PrintDeck(deck);
        } else {
            System.out.println("None");
        }
    }

}
