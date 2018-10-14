# CheckPoint - An Android Application
We (as a team of 3) designed this Android app from scratch in Summer 2016 within 4 days.

# Concept
* This app is designed so that you can explore a city or an area.
* If you have a list of locations you know you want to visit, this app will store that information and display it on a map, as well as tell you when your are near that location.

# Main Map
This part of the app allows you to navigate around an area you don’t know and visit locations that are nearby. It also alerts you when you reach the location you’re looking for.
![main map](image/image1.png)

* Whole app relies on three .csv files containing Names, Latitudes, and Longitudes of locations.
* They are created (if they do not exist) and parsed to plot markers in this activity.
* The distances between your location and the checkpoints are calculated every time your location is updated, and if it is less than 30m, the marker is removed, and the entry is deleted from the list.
