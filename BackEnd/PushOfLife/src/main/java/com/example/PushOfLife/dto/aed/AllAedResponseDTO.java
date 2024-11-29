package com.example.PushOfLife.dto.aed;

import com.example.PushOfLife.entity.AedAvailableEntity;
import com.example.PushOfLife.entity.AedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllAedResponseDTO {

    @JsonProperty(value = "aed_id")
    private Integer aedId;

    @JsonProperty(value = "available_id")
    private Integer availableId;

    public void fromEntity(AedEntity aed, AedAvailableEntity aedAvailable) {
        this.aedId = aed.getId();
        this.availableId = aedAvailable.getId();
    }
}
