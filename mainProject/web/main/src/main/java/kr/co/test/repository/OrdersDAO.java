package kr.co.test.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import kr.co.test.vo.OrdersVO;

@Repository
public class OrdersDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertOrder(OrdersVO ordersVO) {
        System.out.println("Inserting Order : " + ordersVO);

        String insertSql = "INSERT INTO orders (order_id, user_id, total_price, address, name, phone, order_date) "
                         + "VALUES (order_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        // 주문 데이터 삽입
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql);
            ps.setLong(1, ordersVO.getUserId());
            ps.setDouble(2, ordersVO.getTotalPrice());
            ps.setString(3, ordersVO.getAddress());
            ps.setString(4, ordersVO.getName());
            ps.setString(5, ordersVO.getPhone());
            ps.setTimestamp(6, new java.sql.Timestamp(ordersVO.getOrderDate().getTime()));
            return ps;
        });

        // 마지막으로 생성된 order_id를 가져오기
        String selectSql = "SELECT order_seq.CURRVAL FROM dual";
        return jdbcTemplate.queryForObject(selectSql, Long.class);
    }
    public List<OrdersVO> getAllOrders() {
        String selectSql = "SELECT order_id, user_id, order_date, total_price, address, name, phone FROM orders";
        return jdbcTemplate.query(selectSql, (rs, rowNum) -> {
            OrdersVO order = new OrdersVO();
            order.setOrderId(rs.getLong("order_id"));
            order.setUserId(rs.getLong("user_id"));
            order.setOrderDate(rs.getDate("order_date"));
            order.setTotalPrice(rs.getDouble("total_price"));
            order.setAddress(rs.getString("address"));
            order.setName(rs.getString("name"));
            order.setPhone(rs.getString("phone"));
            return order;
        });
    }
    public OrdersVO getOrderById(Long orderId) {
        String selectSql = "SELECT order_id, user_id, order_date, total_price, address, name, phone FROM orders WHERE order_id = ?";
        return jdbcTemplate.queryForObject(selectSql, new Object[]{orderId}, (rs, rowNum) -> {
            OrdersVO order = new OrdersVO();
            order.setOrderId(rs.getLong("order_id"));
            order.setUserId(rs.getLong("user_id"));
            order.setOrderDate(rs.getDate("order_date"));
            order.setTotalPrice(rs.getDouble("total_price"));
            order.setAddress(rs.getString("address"));
            order.setName(rs.getString("name"));
            order.setPhone(rs.getString("phone"));
            return order;
        });
    }

    public void updateOrder(OrdersVO order) {
        String updateSql = "UPDATE orders SET name = ?, phone = ?, address = ?, total_price = ? WHERE order_id = ?";
        jdbcTemplate.update(updateSql, order.getName(), order.getPhone(), order.getAddress(), order.getTotalPrice(), order.getOrderId());
    }

    public void deleteOrder(Long orderId) {
        String deleteSql = "DELETE FROM orders WHERE order_id = ?";
        jdbcTemplate.update(deleteSql, orderId);
    }
    public Double getTotalSales() {
        String selectSql = "SELECT SUM(total_price) FROM orders";
        return jdbcTemplate.queryForObject(selectSql, Double.class);
    }


 // 사용자의 주문 내역을 조회하는 메서드
    public List<OrdersVO> getOrdersByUserId(Long userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, 
            (rs, rowNum) -> {
                OrdersVO order = new OrdersVO();
                order.setOrderId(rs.getLong("order_id"));
                order.setUserId(rs.getLong("user_id"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setTotalPrice(rs.getDouble("total_price"));
                // 추가적으로 필요한 필드들 설정
                return order;
            });
    }
}
