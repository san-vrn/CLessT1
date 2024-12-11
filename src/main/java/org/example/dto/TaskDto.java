package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.example.entity.task.TaskStatus;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDto {

    private Long id;

    private String title;

    private String description;

    @JsonProperty("user_id")
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private TaskStatus status;
}
