import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

function Dashboard() {
  const [blogs, setBlogs] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBlogs = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
      } else {
        try {
          const response = await axios.get("http://localhost:8080/api/blogs/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          setBlogs(response.data);
        } catch (error) {
          console.error("Error fetching blogs:", error);
        }
      }
    };
    fetchBlogs();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  const handleDelete = async (blogId) => {
    const token = localStorage.getItem("token");
    try {
      await axios.delete(`http://localhost:8080/api/blogs/${blogId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setBlogs(blogs.filter((blog) => blog.id !== blogId)); 
    } catch (error) {
      console.error("Failed to delete the blog:", error);
    }
  };

  return (
    <div className="dashboard-container">
      <nav>
        <div className="navbar">
          <span>Blog Site</span>
          <div>
            <button onClick={() => navigate("/create-blog")}>Create New Blog</button>
            <button onClick={() => navigate("/report")}>Report</button>
            <button onClick={handleLogout}>Logout</button>
          </div>
        </div>
      </nav>

      <div className="blog-cards-container">
        {blogs.length > 0 ? (
          blogs.map((blog) => (
            <div key={blog.id} className="blog-card">
              <h3>{blog.blogName}</h3>
              <p>{blog.body}</p>
              <div className="blog-actions">
                <button onClick={() => navigate(`/edit-blog/${blog.id}`)}>Edit</button>
                <button onClick={() => handleDelete(blog.id)}>Delete</button>
              </div>
            </div>
          ))
        ) : (
          <p>No blogs available. Create your first blog!</p>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
