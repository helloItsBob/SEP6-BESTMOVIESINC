// DISPLAY AND CLOSE MOVIE POPUP
import {uid} from './login.js';

const movieContainer = document.getElementById('movieContainer');
const moviePopup = document.getElementById('moviePopup');

const getCastTrailerUrl = "URL_CAST_TRAILER_PLACEHOLDER";

// Attach the click event listener to the movie container
movieContainer.addEventListener('click', function (event) {
    // Check if the clicked element or its parent has the movieCard class
    const posterClick = event.target.closest('.moviePoster');
    if (posterClick) {
        // Get the movie card content
        const movieCardContent = posterClick.parentNode.innerHTML;

        // Set the movie card content to the popup
        const modalContent = moviePopup.querySelector('.moviePopupContent');
        modalContent.innerHTML = movieCardContent;

        // set additional information with the help of movie id
        const movieId = modalContent.querySelector('.movieId').textContent;

        // Reattach event listeners to the icons
        const watchlistIcon = modalContent.querySelector('.watchlistIcon');
        watchlistIcon.addEventListener('click', function () {
            handleIconClick(this, 'add', uid, movieId);
        });

        const favoritesIcon = modalContent.querySelector('.heartIcon');
        favoritesIcon.addEventListener('click', function () {
            handleIconClick(this, 'heart', uid, movieId);
        })

        // Create element to hold a movie trailer
        const movieTrailer = document.createElement('div');
        movieTrailer.classList.add('movieTrailer');
        let trailerUrl = '';

        // Create an element to hold cast of the movie
        const movieCast = document.createElement('div');
        movieCast.classList.add('movieCast');
        let castData = '';


        fetch(getCastTrailerUrl + '?movieId=' + movieId)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Error: ' + response.status);
                }
            })
            .then(data => {
                // Process the movie details
                console.log('Movie details:', data);

                // Showing director
                const director = data.credits.crew.find(member => member.job === 'Director');
                const directorName = director ? director.name : 'Unknown';
                const directorElem = document.createElement('p');
                directorElem.textContent = 'Director: ' + directorName;
                modalContent.appendChild(directorElem);

                // Adding a movie trailer
                const trailerKey = data.videos.results[0].key;
                trailerUrl = 'https://www.youtube.com/embed/' + trailerKey;
                // embedding youtube video
                movieTrailer.innerHTML = `<iframe src="${trailerUrl}" allowfullscreen></iframe>`;
                modalContent.appendChild(movieTrailer);

                // create list element to hold the cast data
                const list = document.createElement('ul');
                castData = data.credits.cast;
                console.log(castData);

                castData.forEach((person) => {
                    const listItem = document.createElement('li');

                    // create element to hold actor poster
                    const personImg = document.createElement('img');
                    if (person.profile_path) {
                        personImg.src = 'https://image.tmdb.org/t/p/w500' + person.profile_path;
                    } else {
                        personImg.src = 'no-poster.png';
                    }
                    listItem.appendChild(personImg);

                    // create element to hold actor name
                    const personName = document.createElement('span');
                    personName.textContent = person.name;
                    personName.style.textDecoration = 'underline';
                    listItem.appendChild(personName);

                    // create element to hold actor character
                    const personCharacter = document.createElement('b');
                    personCharacter.textContent = person.character;
                    listItem.appendChild(personCharacter);

                    list.appendChild(listItem);
                    movieCast.appendChild(list);
                    modalContent.appendChild(movieCast);
                });

                // TODO: Maybe make so actors are clickable and it's possible to see other movies they have starred in

            })
            .catch(error => {
                console.error('Error:', error.message);
            });

        // Display the movie popup
        moviePopup.style.display = 'block';
    }
});

// Add event listener to the document and delegate the click event to the moviePopup
document.addEventListener('click', function (event) {
    if (event.target.id === 'moviePopup') {
        closeMoviePopup();
    }
});

function closeMoviePopup() {
    const moviePopup = document.getElementById('moviePopup');
    moviePopup.style.display = 'none';
}