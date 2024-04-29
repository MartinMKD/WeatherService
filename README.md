1. Check out the project from GitHub

2. Install Java + SBT (I tested on MBP M2 Pro only)

3. Change into the project root directory and run "sbt run" to compile and run the weather REST service

4. Use 'curl' or similar to test the API (e.g.):
   
  curl -s "http://localhost:8080/weather?lat=32.52&lon=-93.75"

  or

  curl -s "http://localhost:8080/weather?lat=32.52&lon=-93.75" | python3 -m json.tool

  for pretty-printed JSON output.
  
5. If you want to use your own OpenWeather API key, edit the 'app-id' parameter in
  'WeatherService/src/main/resources/application.conf' or you can use the embedded one.
