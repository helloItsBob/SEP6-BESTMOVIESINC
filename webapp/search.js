// CODE FOR SEARCH BOX

import {filterMovieProperties, updateMovieDetails} from "./script.js";
import {checkIfUserSignedIn} from "./login.js";

const urlSearch = "URL_SEARCH_PLACEHOLDER";
function searchMovie() {
    const movieTitleInput = document.getElementById('titleInput');
    const movieTitle = movieTitleInput.value;
    const originalPlaceholder = movieTitleInput.placeholder;

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

            // change placeholder with the search result
            movieTitleInput.placeholder = `Results for '${movieTitle}'`;
            // hide message after 3 seconds
            setTimeout(function () {
                registrationModal.style.display = 'none';
                movieTitleInput.placeholder = originalPlaceholder;
            }, 3000);

            checkIfUserSignedIn();

            // Clear the input value
            movieTitleInput.value = '';
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

document.getElementById('searchForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent form submission
    const navbarItems = document.querySelectorAll('.navbar a');
    navbarItems.forEach(function (item) {
        item.classList.remove('activeTab');
    });
    searchMovie();
});