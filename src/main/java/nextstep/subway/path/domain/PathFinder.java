package nextstep.subway.path.domain;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {
    private static final String CAN_NOT_FIND_PATH_WHEN_SOURCE_TARGET_IS_SAME = "출발역과 도착역이 동일한 경우 경로를 조회할 수 없습니다.";
    private static final String CAN_NOT_FIND_WHEN_SOURCE_TARGET_IS_NOT_CONNECT = "출발역과 도착역이 연결되어 있지 않은 경우 경로를 조회할 수 없습니다.";

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initGraph(lines);
    }

    private void initGraph(List<Line> lines) {
        lines.forEach(line -> {
            addVortex(line);
            setEdgeWeight(line);
        });
    }

    private void addVortex(Line line) {
        line.findAssignedStations()
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(Line line) {
        line.getSections()
                .value()
                .forEach(this::addEdgeWeight);
    }

    private void addEdgeWeight(Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public Path findShortestPath(Station source, Station target) {
        validateSourceAndTargetIsSame(source, target);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> stationShortedPath = dijkstraShortestPath.getPath(source, target);
        validateSourceAndTargetIsConnect(stationShortedPath);
        List<Station> stations = stationShortedPath.getVertexList();
        double totalDistance = stationShortedPath.getWeight();

        return new Path(stations, (int) totalDistance);
    }

    private void validateSourceAndTargetIsSame(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(CAN_NOT_FIND_PATH_WHEN_SOURCE_TARGET_IS_SAME);
        }
    }

    private void validateSourceAndTargetIsConnect(GraphPath<Station, DefaultWeightedEdge> stationShortedPath) {
        if (stationShortedPath == null) {
            throw new IllegalArgumentException(CAN_NOT_FIND_WHEN_SOURCE_TARGET_IS_NOT_CONNECT);
        }
    }
}
