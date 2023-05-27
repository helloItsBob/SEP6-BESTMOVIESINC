// ====================  Random movie generation  ========================== //

import {filterMovieProperties, updateMovieDetails} from "./script.js";
import {checkIfUserSignedIn} from "./login.js";

const cloverButton = document.getElementById('randomImg');
const value = document.getElementById('a');
const urlRandomMovies = "URL_RANDOM_MOVIES_PLACEHOLDER";
cloverButton.addEventListener('click', () => {
    // show loading animation
    const loadingOverlay = document.getElementById("loading-overlay");
    loadingOverlay.style.display = "flex";

    fetch(urlRandomMovies + '?number=' + value.value)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to retrieve movie data');
            }
        })
        .then(data => {
            const filteredMovies = filterMovieProperties(data);
            updateMovieDetails(filteredMovies);
            console.log(`random Movies:`, data);

            // hide loading animation
            const loadingOverlay = document.getElementById("loading-overlay");
            loadingOverlay.style.display = "none";

            checkIfUserSignedIn();
        })
        .catch(error => {
            console.error('Error:', error);
        });
});
