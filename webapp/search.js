// CODE FOR SEARCH BOX

import {filterMovieProperties, updateMovieDetails} from "./script.js";

const urlSearch = "URL_SEARCH_PLACEHOLDER";
function searchMovie() {
    const movieTitleInput = document.getElementById('titleInput');
    const movieTitle = movieTitleInput.value;

    // show loading animation
    const loadingOverlay = document.getElementById("loading-overlay");
    loadingOverlay.style.display = "flex";

    fetch(`${urlSearch}?movieTitle=${encodeURIComponent(movieTitle)}`)
        .then(response => response.json())
        .then(data => {
            console.log('Search results:', data);

            const filteredMovies = filterMovieProperties(data);
            updateMovieDetails(filteredMovies);

            // hide loading animation
            const loadingOverlay = document.getElementById("loading-overlay");
            loadingOverlay.style.display = "none";

            // Clear the input value
            movieTitleInput.value = '';
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

document.getElementById('searchForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent form submission
    searchMovie();
});