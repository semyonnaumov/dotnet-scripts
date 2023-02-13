package com.naumov.dotnetscriptsscheduler.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_request_payloads")
public class JobRequestPayload {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_request_payloads_gen")
    @SequenceGenerator(name = "job_request_payloads_gen", sequenceName = "job_request_payloads_seq", allocationSize = 10)
    private Long id;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "script", columnDefinition = "text", nullable = false, updatable = false)
    private String script;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json", updatable = false)
    private JobPayloadConfig jobPayloadConfig;
    @Column(name = "agent_type", nullable = false, updatable = false)
    private String agentType;

    @Override
    public String toString() {
        return "JobRequestPayload{" +
                "id=" + id +
                ", script='" + omitLongString(script) + '\'' +
                ", jobPayloadConfig=" + jobPayloadConfig +
                ", agentType='" + agentType + '\'' +
                '}';
    }
}
