package com.naumov.dotnetscriptsscheduler.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_requests")
public class JobRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_requests_gen")
    @SequenceGenerator(name = "job_requests_gen", sequenceName = "job_requests_seq", allocationSize = 10)
    private Long id;
    @Column(name = "message_id", length = 36, nullable = false, updatable = false, unique = true)
    private String messageId;
    @Column(name = "sender_id", length = 36, updatable = false)
    private String senderId;
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "payload_id", nullable = false, updatable = false)
    private JobRequestPayload payload;

    @Override
    public String toString() {
        return "JobRequest{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", payload=" + payload +
                '}';
    }
}
