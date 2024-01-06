
# Paw Palace

Welcome to Paw Palace, the ultimate pet hotel designed for your furry friends! Whether you're heading out for a vacation or a busy day at work, Paw Palace is the perfect retreat for your beloved pets.
## Tech Stack

* **100% Kotlin**, using coroutines for background thread
* **Splash Screen** API
* **Dagger hilt** for dependency injection
* **Navigation components**

## Folder Structures
The structure module used follows the guidelines of a clean architecture by dividing it into several folders with the following details
- data : Used to handle all of the transactions in the application (mainly used to handle database transaction)
- di : DI stands for Dependency Injection, use to inject class that we will need to use in our application
- features : Handle user interface (divided into components and screens), every screens has subfolder and every subfolder contains at least viewmodel, state, and screen
- utils : Define helper const and functions

## How to run the app
1. Clone this project using ```git clone https://github.com/andriawan24/paw-palace```
2. Open the project using latest android studio
3. Choose emulator and run the app
4. Optional for device with low memory like me: Take a deep breath, make an coffee while waiting for the gradle to finished ><

## Contributing
Please contribute! I'm just getting my hands dirty with Modern Android Development.
There is heavy chance that the code may/may not be correct/holding best practices. I request you to contribute/raise issues/send PRs so I can learn too. You can use the Github Discussion to discuss and ask questions. Or you can reach out to me on email fawaznaufal23@gmail.com

## Feedback
If you have any feedback, please reach out me at fawaznaufal23@gmail.com

