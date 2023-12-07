# Task Master

This mobile app revolves around seamlessly adding tasks and effortlessly viewing your entire task list.

---

## What is new??

- In the home page you will see a RecyclerView for the tasks in the database system (title, state, team name) using DynamoDB, you can scroll and you can tap on it to refactor you to the tasks' details page
- Tasks Details page, will display the state of the task as well as the body of it (description) and the img that u choose 
- You can add you name to be displayed in the home page form the setting page 
- You can add a new task (title u can write your own title or git it from shared text from another application, description, state, select a team for this task, and also you can upload an img for this task and the img can be shared from another applications, also the img u can delete is before saving the task if u do not want it anymore) and it will be added to the DynamoDB as well as displaying in the homePage
- And when you add a new task the application will take you current location (Longitude and Latitude), and will display it as well in the details page
- Added some Espresso testing 
- From the Sitting you can filter the tasks by the team name and the filtration will be displayed in the Home page 
- You can Edit the task title, description, team and state when you click on the task itself 
- In the Details Page now you can convert the text to speech by clicking on the convert button 


## Application screenshots

![homePage.jfif](screenshots%2FhomePage.jfif)

![addTaskPage.jfif](screenshots%2FaddTaskPage.jfif)

**You can add img from share**

![sharingImg.jfif](screenshots%2FsharingImg.jfif)

**The img from share will be displayed in the screen**

![addedImgFromShare.jfif](screenshots%2FaddedImgFromShare.jfif)

![AddTaskSubmitButton](/screenshots/addTaskSubmittion.jfif)

![AllTasksPage](/screenshots/allTasksPage.jpeg)

![taskDetails.jfif](screenshots%2FtaskDetails.jfif)

![sittingPAge.jfif](screenshots%2FsittingPAge.jfif)

## Testing pass screenshot

![Testing](/screenshots/testPass.png)