/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization.decision;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Represents a pending event that requires player decision.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Getter
@EqualsAndHashCode
@ToString
public class PendingPlayerEvent {

    public enum EventType {
        MARRIAGE_PROPOSAL,
        // Add more event types as needed
    }

    public enum EventStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        EXPIRED
    }

    private final String id;
    private final EventType type;
    private final Person initiator;
    private final Person target;
    private final LocalDate eventDate;
    private final String description;
    private EventStatus status;

    // Callbacks for when player makes a decision
    private final Consumer<PendingPlayerEvent> onAccept;
    private final Consumer<PendingPlayerEvent> onReject;

    public PendingPlayerEvent(String id, EventType type, Person initiator, Person target,
                              LocalDate eventDate, String description,
                              Consumer<PendingPlayerEvent> onAccept,
                              Consumer<PendingPlayerEvent> onReject) {
        this.id = id;
        this.type = type;
        this.initiator = initiator;
        this.target = target;
        this.eventDate = eventDate;
        this.description = description;
        this.status = EventStatus.PENDING;
        this.onAccept = onAccept;
        this.onReject = onReject;
    }

    /**
     * Called when player accepts the event.
     */
    public void accept() {
        if (status == EventStatus.PENDING) {
            status = EventStatus.ACCEPTED;
            if (onAccept != null) {
                onAccept.accept(this);
            }
        }
    }

    /**
     * Called when player rejects the event.
     */
    public void reject() {
        if (status == EventStatus.PENDING) {
            status = EventStatus.REJECTED;
            if (onReject != null) {
                onReject.accept(this);
            }
        }
    }

    /**
     * Marks the event as expired.
     */
    public void expire() {
        if (status == EventStatus.PENDING) {
            status = EventStatus.EXPIRED;
        }
    }

    /**
     * Checks if the event is still pending.
     *
     * @return true if pending
     */
    public boolean isPending() {
        return status == EventStatus.PENDING;
    }
}
