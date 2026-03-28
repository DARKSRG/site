package dao;

import models.DiskExchange;
import utils.DBConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiskExchangeDAO {

    private String generateExchangeNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        String sql = "SELECT COUNT(*) FROM disk_exchanges";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("ОБМ-%s-%04d", datePart, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ОБМ-" + datePart + "-0001";
    }

    public boolean createExchange(DiskExchange ex) {
        String sql = "INSERT INTO disk_exchanges (user_id, media_type_id, title, release_year, disk_condition, "
                + "exchange_date, client_info, exchange_number, disk_for_exchange, new_disk) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (ex.getExchangeNumber() == null || ex.getExchangeNumber().trim().isEmpty()) {
                ex.setExchangeNumber(generateExchangeNumber());
            }
            pstmt.setInt(1, ex.getUserId());
            pstmt.setInt(2, ex.getMediaTypeId());
            pstmt.setString(3, ex.getTitle());
            pstmt.setObject(4, ex.getReleaseYear());
            pstmt.setString(5, ex.getDiskCondition());
            pstmt.setDate(6, ex.getExchangeDate());
            pstmt.setString(7, ex.getClientInfo());
            pstmt.setString(8, ex.getExchangeNumber());
            pstmt.setString(9, ex.getDiskForExchange());
            pstmt.setString(10, ex.getNewDisk());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DiskExchange> getExchangesByUserId(int userId) {
        List<DiskExchange> list = new ArrayList<>();
        String sql = "SELECT e.*, mt.type_name AS media_type_name FROM disk_exchanges e "
                + "INNER JOIN media_types mt ON e.media_type_id = mt.id "
                + "WHERE e.user_id = ? ORDER BY e.exchange_date DESC, e.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowBase(rs, false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * История обменов с фильтрами: пользователь (null = все), период по дате обмена, тип носителя (null = все).
     */
    public List<DiskExchange> searchExchanges(Integer userIdFilter, java.sql.Date dateFrom, java.sql.Date dateTo, Integer mediaTypeIdFilter) {
        List<DiskExchange> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.*, mt.type_name AS media_type_name, u.username AS owner_username, u.full_name AS owner_full_name "
                + "FROM disk_exchanges e "
                + "INNER JOIN media_types mt ON e.media_type_id = mt.id "
                + "INNER JOIN users u ON e.user_id = u.id "
                + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (userIdFilter != null) {
            sql.append(" AND e.user_id = ?");
            params.add(userIdFilter);
        }
        if (dateFrom != null) {
            sql.append(" AND e.exchange_date >= ?");
            params.add(dateFrom);
        }
        if (dateTo != null) {
            sql.append(" AND e.exchange_date <= ?");
            params.add(dateTo);
        }
        if (mediaTypeIdFilter != null) {
            sql.append(" AND e.media_type_id = ?");
            params.add(mediaTypeIdFilter);
        }
        sql.append(" ORDER BY e.exchange_date DESC, e.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) p);
                } else if (p instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) p);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowBase(rs, true));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteExchange(int id, int userId) {
        String sql = "DELETE FROM disk_exchanges WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static DiskExchange mapRowBase(ResultSet rs, boolean withUser) throws SQLException {
        DiskExchange ex = new DiskExchange();
        ex.setId(rs.getInt("id"));
        ex.setUserId(rs.getInt("user_id"));
        ex.setMediaTypeId(rs.getInt("media_type_id"));
        ex.setMediaTypeName(rs.getString("media_type_name"));
        ex.setTitle(rs.getString("title"));
        int ry = rs.getInt("release_year");
        if (rs.wasNull()) {
            ex.setReleaseYear(null);
        } else {
            ex.setReleaseYear(ry);
        }
        ex.setDiskCondition(rs.getString("disk_condition"));
        ex.setExchangeDate(rs.getDate("exchange_date"));
        ex.setClientInfo(rs.getString("client_info"));
        ex.setExchangeNumber(rs.getString("exchange_number"));
        ex.setDiskForExchange(rs.getString("disk_for_exchange"));
        ex.setNewDisk(rs.getString("new_disk"));
        ex.setCreatedAt(rs.getTimestamp("created_at"));
        if (withUser) {
            ex.setOwnerUsername(rs.getString("owner_username"));
            ex.setOwnerFullName(rs.getString("owner_full_name"));
        }
        return ex;
    }
}
