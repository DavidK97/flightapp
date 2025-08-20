package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.flightdemo.dtos.FlightDTO;
import dk.cphbusiness.flightdemo.dtos.FlightInfoDTO;
import dk.cphbusiness.utils.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    public static void main(String[] args) {
        try {
            List<FlightDTO> flightList = getFlightsFromFile("flights.json");
            List<FlightInfoDTO> flightInfoDTOList = getFlightInfoDetails(flightList);
            flightInfoDTOList.forEach(System.out::println);

            //Opgave 4.1
            Double result = totalFlightDurationByAirline(flightInfoDTOList, "Royal Jordanian");
            System.out.println("Opgave 4.1: " + result + " timer");

            //Opgave 1
            Double result2 = averageFlightTimeByAirline(flightInfoDTOList, "Royal Jordanian");
            System.out.println("Opgave 1: " + result2 + " minutter");

            //Opgave 2
            /*
            List<FlightInfoDTO> result3 = flightBetween2Airports(flightInfoDTOList,
                    "King Hussein International", "Queen Alia International");
            result3.forEach(System.out::println);

             */

            //Opgave 3
            System.out.println("Opgave 3: ");
            List<FlightInfoDTO> result4 = sortByDepatureTime(flightInfoDTOList,
                    LocalDateTime.parse("2024-08-15T10:05:00"));
            result4.forEach(System.out::println);
            System.out.println("Opgave 3 slut");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<FlightDTO> getFlightsFromFile(String filename) throws IOException {

        ObjectMapper objectMapper = Utils.getObjectMapper();

        // Deserialize JSON from a file into FlightDTO[]
        FlightDTO[] flightsArray = objectMapper.readValue(Paths.get("flights.json").toFile(), FlightDTO[].class);

        // Convert to a list
        List<FlightDTO> flightsList = List.of(flightsArray);
        return flightsList;
    }

    public static List<FlightInfoDTO> getFlightInfoDetails(List<FlightDTO> flightList) {
        List<FlightInfoDTO> flightInfoList = flightList.stream()
                .map(flight -> {
                    LocalDateTime departure = flight.getDeparture().getScheduled();
                    LocalDateTime arrival = flight.getArrival().getScheduled();
                    Duration duration = Duration.between(departure, arrival);
                    FlightInfoDTO flightInfo =
                            FlightInfoDTO.builder()
                                    .name(flight.getFlight().getNumber())
                                    .iata(flight.getFlight().getIata())
                                    .airline(flight.getAirline().getName())
                                    .duration(duration)
                                    .departure(departure)
                                    .arrival(arrival)
                                    .origin(flight.getDeparture().getAirport())
                                    .destination(flight.getArrival().getAirport())
                                    .build();

                    return flightInfo;
                })
                .toList();
        return flightInfoList;
    }


    //Opgave 4.1
    public static Double totalFlightDurationByAirline(List<FlightInfoDTO> flightInfoList, String airlineName) {
        return flightInfoList.stream()
                .filter(f -> f.getAirline() != null)
                .filter(f -> f.getAirline().equals(airlineName))
                .mapToDouble(f -> f.getDuration().toMinutes())
                .sum()/60;
    }

    //Opgave 1
    public static Double averageFlightTimeByAirline(List<FlightInfoDTO> flightInfoList, String airlineName) {
        return flightInfoList.stream()
                .filter(f -> f.getAirline() != null)
                .filter(f -> f.getAirline().equals(airlineName))
                .mapToDouble(f -> f.getDuration().toMinutes())
                .average().orElse(0.0);
    }

    //Opgave 2
    public static List<FlightInfoDTO> flightBetween2Airports (List<FlightInfoDTO> flightInfoList, String origin, String destination) {
        return flightInfoList.stream()
                .filter(f -> f.getOrigin() != null)
                .filter(f -> f.getOrigin().equalsIgnoreCase(origin))
                .filter(f -> f.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    //Opgave 3
    public static List<FlightInfoDTO> sortByDepatureTime (List<FlightInfoDTO> flightInfoList, LocalDateTime time) {
        return flightInfoList.stream()
                .filter(f -> f.getDeparture().isAfter(time))
                .collect(Collectors.toList());
    }


}
