# T03_Team_3_MAD

# Team members

# Zheng Jiong Jie S10195565
# Swah Jian Oon S10195746
# Chua Qi Heng S10195642
# Christopher Tey Weixian S10193226

# Description of app

# This application is one which enables users to view various books and find new books to read. Users can also use this
# application as a platform to post reviews on the books they have read, or save the books they have interest in in a list to refer to later on.
# This application also allows users to manage the books they are interested in, or view the information on other book titles.

# Contributions

# Zheng Jiong Jie (Role: Leader)
=======================
#Things Implemented:
---------------------
#Set up how fragments will be displayed
#Created User page
#Created Logged in user page
#Created View all user's favourite books page
#Created Home page
#Allow user to view own data or other user's data(load data from database)
#Update user data to database(update content in database)
#Allow users to make changes to user's profile(image/name/description)
#Allow users to upload new profile picture from gallery and add to internal storage
#Allow users to follow other users(and store in database)
#Allow users to add books to their favourite books list(and store in database)
#Recyclerview adapters for "AdapterBookMain", "AdapterFavBooksList", "AdapterReviewForUSer", "AdapterUserMain"
#Implemented on-click listeners which brings users to where they clicked for the various adapters
#Allow users to manage their current list of books(remove/add)
#Created Bottom navigation bar
#LogOut
#Custom Backstack
#Navigation

# Swah Jian Oon
=======================
#Things Implemented:
---------------------
#Imported precreated database into android studio
#Created local database - DatabaseAccess & DatabaseOpenHelper
#Created methods to extract data from database
#Created methods to store data into database
#Created adapters for AdapterReview
#Created review page
#Created add review page 
#Linked book information fragment to its review page 
#Linked clicking on name of reviewer on review page to the reviewer's profile

# Chua Qi Heng
=======================
#Things Implemented:
---------------------
#Added parcelable to Book, Author and User classes so people can bring objects to another fragment
#Added clicking functionality to adapters
#Added search functionality
#Added method to save images to internal storage
#Added method to set image with String.
#Added recycler view for author to show published books.
#Created Author Info page and set all the information inside
#Passed object information to Book, Author and User Info so that other people can use the info
#Created methods to search database for the search functionality
#Created User, Author, Book and Searchclass models


# Christopher Tey Weixian
=======================
#Things Implemented:
---------------------
#Created Shared Preferences to check if user is logged in or not
#Register Page
#Created Firebase database for creation of new users
#Login Page
#Created method to insert user's data from firebase database into local database
#Created method to check if user's data existed in the local database or not
#Linked Login page to register page and home page 
#Created Genre recycler view in homepage.
#Created a recycler view for books based on the genre that they clicked on the previous page
#Linked the 2 recycler view fragments to each other and linked the bookbygenre fragment to bookinfo fragment
#Created the recycler view for genre in the bookinfo fragment
#Created the adaptors of "AdaptorGenreInHomeFragment" ,"AdaptorGenre" and "AdaptorToViewBookBasedOnGenre".
