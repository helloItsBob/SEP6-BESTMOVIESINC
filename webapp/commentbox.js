// *************************  COMMENT BOX  ******************************** //

let selectedRating = null;

export function makeCommentBox(uid, movieId) {
    const commentContainer = document.createElement('div');

    // text area for the input
    const inputBox = document.createElement('textarea');
    inputBox.classList.add('inputBox');
    inputBox.placeholder = 'What do you think of this movie?';
    commentContainer.appendChild(inputBox);

    // rating bar
    const rating = createRating();
    commentContainer.appendChild(rating);

    // submit button
    const button = document.createElement('button');
    button.classList.add('subButton');
    button.innerText = 'Submit';
    commentContainer.appendChild(button);

    function submitComment() {
        if (inputBox.value.length !== 0 && selectedRating !== null) {
            const reviewElement = document.createElement('div');
            reviewElement.classList.add('userReviewDiv');

            const username = document.createElement('p');
            username.id = 'usernameElement';
            username.textContent = 'You';
            reviewElement.appendChild(username);

            const date = document.createElement('p');
            date.id = 'dateElement';

            // get '2023-05-29' format
            const today = new Date();
            const year = today.getFullYear();
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const day = String(today.getDate()).padStart(2, '0');

            date.textContent = `${year}-${month}-${day}`;
            reviewElement.appendChild(date);

            const commentText = document.createElement('p');
            commentText.id = 'commentElement';
            commentText.textContent = inputBox.value;
            reviewElement.appendChild(commentText);

            const movieRating = document.createElement('p');
            movieRating.id = 'ratingElement';
            for (let i = 0; i < selectedRating; i++) {
                const star = document.createElement('span');
                star.classList.add('star', 'active');
                star.innerHTML = '&#9733;';
                movieRating.appendChild(rating)
            }
            reviewElement.appendChild(movieRating);

            const userReviews = document.getElementById('userReviews');
            const firstReview = userReviews.firstChild;

            userReviews.insertBefore(reviewElement, firstReview);

            // send the request to db
            const urlComments = "URL_POST_COMMENTS_PLACEHOLDER";
            fetch(urlComments + '?uid=' + uid + '&content=' + inputBox.value
                + '&rating=' + selectedRating + '&movieId=' + movieId, {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        console.log(response.text());
                        console.log("The comment has been saved!")
                    } else {
                        console.log(response)
                        throw new Error(`Error: ${response.status}`);
                    }
                })
                .catch(error => {
                    console.error('Error:', error.message);
                });

            inputBox.readOnly = true;

        } else {
            inputBox.placeholder = 'Comment or rating cannot be empty!';
            setTimeout(function () {
                inputBox.placeholder = 'What do you think of this movie?';
            }, 2000);
        }

        inputBox.value = '';
    }

    button.addEventListener('click', submitComment);

    return commentContainer;
}

// Function to create rating elements
function createRating() {
    const ratingDiv = document.createElement('div');
    ratingDiv.innerHTML = '';

    for (let i = 1; i <= 5; i++) {
        const star = document.createElement('span');
        star.classList.add('star');
        star.innerHTML = '&#9734;';
        star.setAttribute('data-value', i);
        ratingDiv.appendChild(star);
    }

    // Add event listeners to stars
    const stars = ratingDiv.querySelectorAll('.star');
    stars.forEach((star, index) => {
        star.addEventListener('click', () => {
            const value = star.getAttribute('data-value');
            // Perform any desired action with the selected rating value
            console.log(`Selected rating: ${value}`);

            // Update the active state of stars
            for (let i = 0; i <= index; i++) {
                stars[i].classList.add('filledStar');
                stars[i].innerHTML = '&#9733;';
            }
            for (let i = index + 1; i < stars.length; i++) {
                stars[i].classList.remove('filledStar');
            }
            selectedRating = value;
        });
    });

    return ratingDiv;
}
