# LondonLocationFinder
A Java and Spring Boot application leveraging a neo4j database and a few APIs. A system that can find all london tube stations that will be within the maximum commuting time you specify for one or more people. 

Takes a specific query, a sample can be found in the resources folder in the tech.amcg.llf package. A front end to take user input, create this query and model the response from this application in an elegant, simple and useful design is under construction and can soon be found here and on my website @ amcg.tech/llf.

Currently hosted on heroku at amcg-llf.herokuapp.com. Feel free to utilise the back end of this system, though it isn't yet finished. A postman collection for the 4 endpoints of this system can be found linked below, or just use your browser for the get requests.

https://www.getpostman.com/collections/563c55f1e60472a4dfd2

Apologies if there are issues when testing this system, it is still a work in progress and currently it is utilising the free tiers heroku offers, meanining responses can be very slow when it has not received a request for a while, sometimes even timing out without a response.

#To Do
Increase travel times between stations by a certain number of minutes for each required line change to complete each journey.

Return the steps taken for each journey returned in the core response.

Increase test coverage.

Add support for automatically excluding half zones between 2 zones that have been excluded.
