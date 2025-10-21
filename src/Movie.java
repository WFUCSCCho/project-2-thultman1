
/****************************************************
 * @file: Movie.java
 * @description: Represents a Movie record parsed from CSV. Comparable by alphabetical name so Movies can be stored in a BST keyed by name.
 * @author: Tim Hultman
 * @date: 9/24/25
 ****************************************************/
public class Movie implements Comparable<Movie> {
    private String name;
    private int year;
    private String duration;
    private String genre;
    private double rating;
    private String description;
    private String director;
    private String stars;

    /**
     * Default constructor
     * Parameter none
     * Return void
     */
    public Movie() {}

    /**
     * Parameter constructor
     * Parameter String name, int year, String duration, String genre, double rating, String description, String director, String stars
     * Return void
     */
    public Movie(String movie, int year, String length, String genre, double score, String description, String director, String actors) {
        this.name = movie;
        this.year = year;
        this.duration = length;
        this.genre = genre;
        this.rating = score;
        this.description = description;
        this.director = director;
        this.stars = actors;
    }

    // copy constructor
    public Movie(Movie o) {
        this(o.name, o.year, o.duration, o.genre, o.rating, o.description, o.director, o.stars);
    }


    // Getters for all fields
    // Return String name, int year, String duration, String genre, double rating, String description, String director, String stars
    public String getName() {
        return name; }
    public int getYear() {
        return year; }
    public String getDuration() {
        return duration; }
    public String getGenre() {
        return genre; }
    public double getRating() {
        return rating; }
    public String getDescription() {
        return description; }
    public String getDirector() {
        return director; }
    public String getStars() {
        return stars; }


    // Setters for all fields
    // Parameter String name, int year, String duration, String genre, double rating, String description, String director, String stars
    public void setName(String name) {
        this.name = name; }
    public void setYear(int year) {
        this.year = year; }
    public void setDuration(String duration) {
        this.duration = duration; }
    public void setGenre(String genre) {
        this.genre = genre; }
    public void setRating(double rating) {
        this.rating = rating; }
    public void setDescription(String description) {
        this.description = description; }
    public void setDirector(String director) {
        this.director = director; }
    public void setStars(String stars) {
        this.stars = stars; }

    /**
     * toString Return key details as formatted string
     * Parameter none
     * Return String
     */
    @Override
    public String toString() {
        return name + " (" + year + "), " + duration + " - " + genre + " - " + rating
                + " | " + description + " | Directed by: " + director + " | Stars: " + stars;
    }

    /**
     * equals Compare by movie name, not case sensititve
     * Parameter Object obj
     * Return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this ==obj) {
            return true;
        }
        Movie other = (Movie) obj;
        return this.name.equalsIgnoreCase(other.name);
    }

    /**
     * compareTo Alphabetical by name (case-insensitive)
     * Parameter Movie other
     * Return int
     */
    @Override
    public int compareTo(Movie other) {
        return this.name.compareToIgnoreCase(other.name);
    }
}
