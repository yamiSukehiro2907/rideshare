package entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rides")
@Data
public class Ride {
    @Id
    private String rideId;

    private String userId;

    private String pickUpLocation;

    private String dropLocation;

    private
}
