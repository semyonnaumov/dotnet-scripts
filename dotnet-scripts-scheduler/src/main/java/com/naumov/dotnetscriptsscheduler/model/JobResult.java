package com.naumov.dotnetscriptsscheduler.model;

import jakarta.persistence.*;
import lombok.*;

import static com.naumov.dotnetscriptsscheduler.util.StringUtil.omitLongString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_results")
public class JobResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_results_gen")
    @SequenceGenerator(name = "job_results_gen", sequenceName = "job_results_seq", allocationSize = 10)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "finished_with", length = 20, nullable = false, updatable = false)
    private JobCompletionStatus finishedWith;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "stdout", columnDefinition = "text", updatable = false)
    private String stdout;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "stderr", columnDefinition = "text", updatable = false)
    private String stderr;

    @Override
    public String toString() {
        return "JobResult{" +
                "id=" + id +
                ", finishedWith=" + finishedWith +
                ", stdout='" + omitLongString(stdout) + '\'' +
                ", stderr='" + omitLongString(stderr) + '\'' +
                '}';
    }

    public enum JobCompletionStatus {
        SUCCEEDED,
        FAILED,
        TIME_LIMIT_EXCEEDED
    }
}
